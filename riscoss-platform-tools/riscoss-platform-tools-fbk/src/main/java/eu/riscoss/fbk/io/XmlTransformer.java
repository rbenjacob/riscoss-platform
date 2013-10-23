package eu.riscoss.fbk.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlTransformer {

	private static Level loggerLevel = Level.OFF;
	private final Logger logger = Logger.getAnonymousLogger();

	public void IStarML2InnerXml(File istarml, File innerXml)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException {
		configLogger(logger);
		if (!istarml.exists()) {
			throw new IllegalArgumentException(
					"The iStarML file does not exist: " + istarml);
		} else if (innerXml.exists()) {
			throw new IllegalArgumentException(
					"The XML output file already exists: " + innerXml);
		} else {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(istarml, new iStarMLHandler<File>(innerXml));
		}
	}

	public String IStarML2InnerXml(String istarml)
			throws ParserConfigurationException, SAXException, IOException,
			UnsupportedEncodingException {
		StringWriter sw = new StringWriter();
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(new ByteArrayInputStream(istarml.getBytes("UTF-8")),
				new iStarMLHandler<StringWriter>(sw));
		return sw.toString();
	}

	private static void configLogger(Logger logger) {
		logger.setUseParentHandlers(false);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {

			@Override
			public String format(LogRecord record) {
				long millis = record.getMillis();
				String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
						.format(new Date(millis));
				Level level = record.getLevel();
				String location = record.getSourceClassName() + "."
						+ record.getSourceMethodName() + "()";
				String message = record.getMessage();
				return date + " " + level + ": " + message + "\t\t[" + location
						+ "]\n";
			}
		});
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
		// TODO args config
		logger.setLevel(loggerLevel);
	}

	private static class iStarMLHandler<Output> extends DefaultHandler {

		private final Logger logger = Logger.getAnonymousLogger();
		private static final String ACTOR_CONTAINS_TAG = "actorContains";
		private static final String TASK_TAG = "task";
		private static final String GOAL_TAG = "goal";
		private static final String SOFTGOAL_TAG = "softgoal";
		private static final String ACTOR_TAG = "actor";
		private static final List<String> UNINFORMATIVE_TAGS = Arrays.asList(
				"istarml", "boundary");
		private static final String CONSTRAINT_TAG = "constraint";
		private static final String DECOMPOSITION_TAG = "decomposition";
		private static final String PROXY_TAG = "proxy";
		private static final String MEANS_END_TAG = "meansEnd";
		private static final String CONTRIBUTION_TAG = "contribution";
		private static final String DEPENDENCY_TAG = "dependency";
		private static final String DEPENDER_TAG = "depender";
		private static final String DEPENDEE_TAG = "dependee";
		private static final String RESOURCE_TAG = "resource";
		private Document doc;
		private Element entities;
		private Element relationships;
		private final Output output;
		private int outputIndex = 0;
		private final LinkedList<Tag> stack = new LinkedList<Tag>();
		private final Map<Tag, Collection<String>> cachedUpdates = new HashMap<Tag, Collection<String>>();
		private Locator locator;

		public iStarMLHandler(Output output) {
			this.output = output;
			configLogger(logger);
		}

		@Override
		public void setDocumentLocator(final Locator locator) {
			this.locator = locator;
		}

		private void initDocument() {
			try {
				doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.newDocument();
			} catch (ParserConfigurationException e) {
				throw new RuntimeException(e);
			}
			Element root = doc.createElement("riscoss");
			Element model = doc.createElement("model");
			model.setAttribute("type", "goalmodel");
			entities = doc.createElement("entities");
			relationships = doc.createElement("relationships");

			doc.appendChild(root);
			root.appendChild(model);
			model.appendChild(entities);
			model.appendChild(relationships);
		}

		private void saveDocument()
				throws TransformerFactoryConfigurationError, IOException {
			logger.info("Processing cached updates...");
			for (Entry<Tag, Collection<String>> entry : cachedUpdates
					.entrySet()) {
				Tag relationTag = entry.getKey();
				logger.info("Tag: " + relationTag);
				for (String refId : entry.getValue()) {
					addSource(retrieveTag(refId), relationTag);
				}
				Element node = relationTag.getNode();
				String sources = relationTag.get("source");
				if (node == null) {
					throw new RuntimeException("No existing node for "
							+ relationTag);
				} else {
					node.setAttribute("source", sources);
				}
			}
			logger.info("Updates done.");

			try {
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
						"no");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				transformer.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "2");

				DOMSource source = new DOMSource(doc);
				OutputStream stream = generateOutputStream();
				StreamResult result = new StreamResult(stream);
				// StreamResult result = new StreamResult(System.out);
				transformer.transform(source, result);
			} catch (TransformerConfigurationException e) {
				throw new RuntimeException(e);
			} catch (TransformerException e) {
				throw new RuntimeException(e);
			}
		}

		private OutputStream generateOutputStream() throws IOException,
				FileNotFoundException {
			if (output instanceof File) {
				File file = (File) output;
				String name = outputIndex == 0 ? file.getName() : file
						.getName().replaceAll("\\.xml$",
								"(" + outputIndex + ").xml");
				File folder = file.getParentFile();
				String path = new File(folder, name).getCanonicalPath();
				return new FileOutputStream(path);
			} else if (output instanceof StringWriter) {
				return new ByteArrayOutputStream() {
					@Override
					public synchronized void write(byte[] b, int off, int len) {
						((StringWriter) output).append(new String(b, off, len));
					}
				};
			} else {
				logger.warning("No managed output provided (" + output
						+ "), use the default one.");
				return System.out;
			}
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			logger.info("Stack > " + stack);
			logger.info(qName + " (line " + locator.getLineNumber() + ")");
			if (UNINFORMATIVE_TAGS.contains(qName)) {
				// no information here, ignore it
			} else if (qName.equals("diagram")) {
				initDocument();
			} else if (qName.equals("actor")) {
				initActor(attributes);
			} else if (qName.equals("ielement")) {
				String type = attributes.getValue("type");
				logger.info(type);
				if (type == null && attributes.getValue("iref") != null) {
					initProxy(attributes);
				} else if (type.equals("goal")) {
					initGoal(attributes);
				} else if (type.equals("softgoal")) {
					initSoftgoal(attributes);
				} else if (type.equals("task")) {
					initTask(attributes);
				} else if (type.equals("resource")) {
					initResource(attributes);
				} else if (type.equals("constraint")) {
					initConstraint(attributes);
				} else {
					throw new IllegalArgumentException(
							"Unmanaged ielement type: " + type);
				}
			} else if (qName.equals("ielementLink")) {
				String type = attributes.getValue("type");
				logger.info(type);
				if (type.equals("decomposition")) {
					initDecomposition(attributes);
				} else if (type.equals("means-end")) {
					initMeansEnd(attributes);
				} else if (type.equals("contribution")) {
					initContribution(attributes);
				} else {
					throw new IllegalArgumentException(
							"Unmanaged ielementLink type: " + type);
				}
			} else if (qName.equals("dependency")) {
				initDependency(attributes);
			} else if (qName.equals("depender")) {
				initDepender(attributes);
			} else if (qName.equals("dependee")) {
				initDependee(attributes);
			} else {
				throw new IllegalArgumentException("Unmanaged tag: " + qName);
			}
			logger.info("Stack < " + stack);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			super.endElement(uri, localName, qName);
			logger.info("Stack > " + stack);
			logger.info("/" + qName + " (line " + locator.getLineNumber() + ")");
			String id = stack.isEmpty() ? null : stack.peek().get("name");
			if (UNINFORMATIVE_TAGS.contains(qName)) {
				// no information here, ignore it
			} else if (qName.equals("diagram")) {
				try {
					saveDocument();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else if (qName.equals("actor")) {
				saveActor();
				((Element) entities.getLastChild()).setAttribute("istarmlId",
						id);
			} else if (qName.equals("ielement")) {
				String type = stack.peek().getName();
				if (type.equals(GOAL_TAG)) {
					saveGoal();
					((Element) entities.getLastChild()).setAttribute(
							"istarmlId", id);
				} else if (type.equals(SOFTGOAL_TAG)) {
					saveSoftgoal();
					((Element) entities.getLastChild()).setAttribute(
							"istarmlId", id);
				} else if (type.equals(TASK_TAG)) {
					saveTask();
					((Element) entities.getLastChild()).setAttribute(
							"istarmlId", id);
				} else if (type.equals(RESOURCE_TAG)) {
					saveResource();
					((Element) entities.getLastChild()).setAttribute(
							"istarmlId", id);
				} else if (type.equals(CONSTRAINT_TAG)) {
					saveConstraint();
					((Element) entities.getLastChild()).setAttribute(
							"istarmlId", id);
				} else if (type.equals(PROXY_TAG)) {
					saveProxy();
					((Element) entities.getLastChild()).setAttribute(
							"istarmlId", id);
				} else {
					throw new IllegalArgumentException(
							"Unmanaged ielement type: " + type);
				}
			} else if (qName.equals("ielementLink")) {
				String type = stack.peek().getName();
				if (type.equals(DECOMPOSITION_TAG)) {
					saveDecomposition();
				} else if (type.equals(MEANS_END_TAG)) {
					saveMeansEnd();
				} else if (type.equals(CONTRIBUTION_TAG)) {
					saveContribution();
				} else {
					throw new IllegalArgumentException(
							"Unmanaged ielementLink type: " + type);
				}
			} else if (qName.equals(DEPENDENCY_TAG)) {
				saveDependency();
			} else if (qName.equals(DEPENDER_TAG)) {
				saveDepender();
			} else if (qName.equals(DEPENDEE_TAG)) {
				saveDependee();
			} else {
				throw new IllegalArgumentException("Unmanaged tag: " + qName);
			}
			logger.info("Stack < " + stack);
		}

		private void initActor(Attributes attributes) {
			stack.push(new Tag(ACTOR_TAG, attributes));
		}

		// private String extractId( Tag tag )
		// {
		// return tag.get("name").replaceAll( "[^a-zA-Z0-9\\s+]", "_"
		// ).replaceAll( "\\s+" , "_" );
		// }

		private void saveActor() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != ACTOR_TAG) {
				throw new IllegalStateException("Expected " + ACTOR_TAG
						+ " but got " + name);
			} else {
				Element actor = doc.createElement(name);
				// goal.setAttribute("id", "" + tag.getId());
				actor.setAttribute("id", tag.getId()); // tag.get("name"));
				actor.setAttribute("label", tag.get("name"));
				// actor.setAttribute("type",
				// tag.getAttributes().getValue("type"));
				entities.appendChild(actor);
			}
		}

		private void initGoal(Attributes attributes) {
			stack.push(new Tag(GOAL_TAG, attributes));
		}

		private void saveGoal() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != GOAL_TAG) {
				throw new IllegalStateException("Expected " + GOAL_TAG
						+ " but got " + name);
			} else {
				Element goal = doc.createElement(name);
				// goal.setAttribute("id", "" + tag.getId());
				goal.setAttribute("id", tag.getId()); // "[^\\p{L}\\p{N}]", "_"
														// ) );
				goal.setAttribute("label", tag.get("name"));
				// actor.setAttribute("type",
				// tag.getAttributes().getValue("type"));
				entities.appendChild(goal);

				saveActorContainerRelationship(tag);
				updateRelationship(tag);
			}
		}

		private void initSoftgoal(Attributes attributes) {
			stack.push(new Tag(SOFTGOAL_TAG, attributes));
		}

		private void saveSoftgoal() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != SOFTGOAL_TAG) {
				throw new IllegalStateException("Expected " + SOFTGOAL_TAG
						+ " but got " + name);
			} else {
				Element goal = doc.createElement(name);
				// goal.setAttribute("id", "" + tag.getId());
				goal.setAttribute("id", tag.getId());
				goal.setAttribute("label", tag.get("name"));
				// actor.setAttribute("type",
				// tag.getAttributes().getValue("type"));
				entities.appendChild(goal);

				saveActorContainerRelationship(tag);
				updateRelationship(tag);
			}
		}

		private void initTask(Attributes attributes) {
			stack.push(new Tag(TASK_TAG, attributes));
		}

		private void saveTask() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != TASK_TAG) {
				throw new IllegalStateException("Expected " + TASK_TAG
						+ " but got " + name);
			} else {
				Element task = doc.createElement(name);
				// task.setAttribute("id", "" + tag.getId());
				task.setAttribute("id", tag.getId());
				task.setAttribute("label", tag.get("name"));
				// actor.setAttribute("type",
				// tag.getAttributes().getValue("type"));
				entities.appendChild(task);

				saveActorContainerRelationship(tag);
				updateRelationship(tag);
			}
		}

		private void initResource(Attributes attributes) {
			stack.push(new Tag(RESOURCE_TAG, attributes));
		}

		private void saveResource() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != RESOURCE_TAG) {
				throw new IllegalStateException("Expected " + RESOURCE_TAG
						+ " but got " + name);
			} else {
				Element resource = doc.createElement(name);
				// goal.setAttribute("id", "" + tag.getId());
				resource.setAttribute("id", tag.getId());
				resource.setAttribute("label", tag.get("name"));
				// actor.setAttribute("type",
				// tag.getAttributes().getValue("type"));
				entities.appendChild(resource);

				saveActorContainerRelationship(tag);
				updateRelationship(tag);
			}
		}

		private void initConstraint(Attributes attributes) {
			stack.push(new Tag(CONSTRAINT_TAG, attributes));
		}

		private void saveConstraint() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != CONSTRAINT_TAG) {
				throw new IllegalStateException("Expected " + CONSTRAINT_TAG
						+ " but got " + name);
			} else {
				Element constraint = doc.createElement(name);
				constraint.setAttribute("id", tag.getId());
				constraint.setAttribute("label", tag.get("name"));
				// actor.setAttribute("type",
				// tag.getAttributes().getValue("type"));
				entities.appendChild(constraint);

				saveActorContainerRelationship(tag);
				updateRelationship(tag);
			}
		}

		private void initDecomposition(Attributes attributes) {
			stack.push(new Tag(DECOMPOSITION_TAG, attributes));
		}

		private void saveDecomposition() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != DECOMPOSITION_TAG) {
				throw new IllegalStateException("Expected " + DECOMPOSITION_TAG
						+ " but got " + name);
			} else {
				Element relation = doc.createElement(name);
				relation.setAttribute("operator", tag.get("value"));
				String targetProxy = tag.get("iref");
				if (targetProxy == null) {
					relation.setAttribute("target", "" + stack.peek().getId());
					relation.setAttribute("source", tag.get("source"));
					if (cachedUpdates.containsKey(tag)) {
						tag.setNode(relation);
						logger.warning("Cached sources are available, they will be retrieved at the end of the parsing.");
					} else if (relation.getAttribute("source").isEmpty()) {
						throw new IllegalStateException(
								"No source found for the decomposition: "
										+ relation);
					} else {
						// all data set, nothing more to do
					}
				} else {
					relation.setAttribute("target", ""
							+ retrieveId(targetProxy));
					relation.setAttribute("source", "" + stack.peek().getId());
				}
				relationships.appendChild(relation);
			}
		}

		private void initMeansEnd(Attributes attributes) {
			stack.push(new Tag(MEANS_END_TAG, attributes));
		}

		private void saveMeansEnd() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != MEANS_END_TAG) {
				throw new IllegalStateException("Expected " + MEANS_END_TAG
						+ " but got " + name);
			} else {
				Element relation = doc.createElement(name);
				String targetProxy = tag.get("iref");
				if (targetProxy == null) {
					relation.setAttribute("target", "" + stack.peek().getId());
					relation.setAttribute("source", tag.get("source"));
					if (cachedUpdates.containsKey(tag)) {
						tag.setNode(relation);
						logger.warning("Cached sources are available, they will be retrieved at the end of the parsing.");
					} else if (relation.getAttribute("source").isEmpty()) {
						throw new IllegalStateException(
								"No source found for the means-end: "
										+ relation);
					} else {
						// all data set, nothing more to do
					}
				} else {
					relation.setAttribute("target", ""
							+ retrieveId(targetProxy));
					relation.setAttribute("source", "" + stack.peek().getId());
				}
				relationships.appendChild(relation);
			}
		}

		private void initContribution(Attributes attributes) {
			stack.push(new Tag(CONTRIBUTION_TAG, attributes));
		}

		private void saveContribution() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != CONTRIBUTION_TAG) {
				throw new IllegalStateException("Expected " + CONTRIBUTION_TAG
						+ " but got " + name);
			} else {
				Element relation = doc.createElement(name);
				String targetProxy = tag.get("iref");
				if (targetProxy == null) {
					relation.setAttribute("target", "" + stack.peek().getId());
					relation.setAttribute("source", tag.get("source"));
					if (cachedUpdates.containsKey(tag)) {
						tag.setNode(relation);
						logger.warning("Cached sources are available, they will be retrieved at the end of the parsing.");
					} else if (relation.getAttribute("source").isEmpty()) {
						throw new IllegalStateException(
								"No source found for the contribution: "
										+ relation);
					} else {
						// all data set, nothing more to do
					}
				} else {
					relation.setAttribute("target", ""
							+ retrieveId(targetProxy));
					relation.setAttribute("source", "" + stack.peek().getId());
				}
				relationships.appendChild(relation);
			}
		}

		private void initDependency(Attributes attributes) {
			stack.push(new Tag(DEPENDENCY_TAG, attributes));
		}

		private void saveDependency() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != DEPENDENCY_TAG) {
				throw new IllegalStateException("Expected " + DEPENDENCY_TAG
						+ " but got " + name);
			} else {
				Element depender = doc.createElement("dependee");
				depender.setAttribute("source", "" + stack.peek().getId());
				depender.setAttribute("target", tag.get("depender"));
				// depender.setAttribute("dependee", tag.get("dependee"));
				relationships.appendChild(depender);

				Element dependee = doc.createElement("depender");
				dependee.setAttribute("target", "" + stack.peek().getId());
				// dependee.setAttribute("depender", tag.get("depender"));
				dependee.setAttribute("source", tag.get("dependee"));
				relationships.appendChild(dependee);

				// Element dependency = doc.createElement(name);
				// dependency.setAttribute("dependum", "" +
				// stack.peek().getId());
				// dependency.setAttribute("depender", tag.get("depender"));
				// dependency.setAttribute("dependee", tag.get("dependee"));
				// relationships.appendChild(dependency);
			}
		}

		private void initDepender(Attributes attributes) {
			stack.push(new Tag(DEPENDER_TAG, attributes));
		}

		private void saveDepender() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != DEPENDER_TAG) {
				throw new IllegalStateException("Expected " + DEPENDER_TAG
						+ " but got " + name);
			} else {
				String ref = tag.get("iref");
				ref = ref == null ? tag.get("aref") : ref;
				String id = retrieveId(ref);
				stack.peek().add("depender", "" + id);
				logger.fine("< " + id);
			}
		}

		private void initDependee(Attributes attributes) {
			stack.push(new Tag(DEPENDEE_TAG, attributes));
		}

		private void saveDependee() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != DEPENDEE_TAG) {
				throw new IllegalStateException("Expected " + DEPENDEE_TAG
						+ " but got " + name);
			} else {
				String ref = tag.get("iref");
				ref = ref == null ? tag.get("aref") : ref;
				String id = retrieveId(ref);
				stack.peek().add("dependee", "" + id);
				logger.fine("< " + id);
			}
		}

		private void initProxy(Attributes attributes) {
			stack.push(new Tag(PROXY_TAG, attributes));
		}

		private void saveProxy() {
			Tag tag = stack.pop();
			String name = tag.getName();
			if (name != PROXY_TAG) {
				throw new IllegalStateException("Expected " + PROXY_TAG
						+ " but got " + name);
			} else {
				updateRelationship(tag);
			}
		}

		private void saveActorContainerRelationship(Tag targetTag) {
			for (int i = 0; i < stack.size(); i++) {
				Tag tag2 = stack.get(i);
				if (tag2.getName().equals(ACTOR_TAG)) {
					Element rel = doc.createElement(ACTOR_CONTAINS_TAG);
					rel.setAttribute("source", "" + tag2.getId());
					rel.setAttribute("target", "" + targetTag.getId());
					relationships.appendChild(rel);
					break;
				} else {
					continue;
				}
			}
		}

		private void updateRelationship(Tag sourceTag) {
			if (stack.isEmpty()) {
				// nothing to update
			} else {
				Tag relationTag = stack.get(0);
				// TODO replace name-based by class-based
				if (relationTag.getName().equals(DECOMPOSITION_TAG)
						|| relationTag.getName().equals(MEANS_END_TAG)
						|| relationTag.getName().equals(CONTRIBUTION_TAG)) {
					if (sourceTag.getName().equals(PROXY_TAG)) {
						if (cachedUpdates.containsKey(relationTag)) {
							// already initialized
						} else {
							cachedUpdates.put(relationTag,
									new LinkedList<String>());
						}
						cachedUpdates.get(relationTag).add(
								sourceTag.get("iref"));
					} else {
						addSource(sourceTag, relationTag);
					}
				} else {
					logger.fine("(" + relationTag + ")");
					// not in a relationship definition, ignore it
				}
			}
		}

		private void addSource(Tag sourceTag, Tag relationTag) {
			logger.fine("> " + relationTag.get("source"));
			relationTag.add("source", "" + sourceTag.getId());
			logger.fine("< " + relationTag.get("source"));
		}

		private Tag retrieveTag(String istarmlId) {
			return Tag.idMap.get(istarmlId);
		}

		private String retrieveId(String istarmlId) {
			return retrieveTag(istarmlId).getId();
		}
	}

	private static class Tag {
		private static final Map<String, Tag> idMap = new HashMap<String, Tag>();
		@SuppressWarnings("unused")
		private static int nextID = 0;
		private String id;
		private final String name;
		private final Properties properties = new Properties();
		private Element node;

		private String extractId(Attributes attr) {
			return attr.getValue( "id" );
			
//			String val = attr.getValue("name");
//			if (val == null)
//				return "" + (++nextID);
//			val = val.replaceAll("[^a-zA-Z0-9\\s+]", "_").replaceAll("\\s+",
//					"_");
//			while (val.startsWith("_"))
//				val = val.substring(1);
//			return val.toLowerCase();
		}

		public void setNode(Element node) {
			this.node = node;
		}

		public Element getNode() {
			return node;
		}

		public Tag(String name, Attributes attributes) {
			this.name = name;
			this.id = extractId(attributes);
			for (int i = 0; i < attributes.getLength(); i++) {
				String field = attributes.getQName(i);
				String value = attributes.getValue(i);
				this.properties.setProperty(field, value);
				if (field.equals("id")) {
					idMap.put(value, this);
				} else {
					// no specific treatment
				}
			}
		}

		public void add(String property, String append) {
			String value = properties.getProperty(property, "");
			value = value.isEmpty() ? append : value + "," + append;
			properties.setProperty(property, value);
		}

		public String getName() {
			return name;
		}

		public String get(String property) {
			return properties.getProperty(property);
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException, TransformerException {
		if (args.length >= 2) {
			XmlTransformer transformer = new XmlTransformer();

			boolean isLogActivated = args.length == 3 && args[2].equals("-log");
			XmlTransformer.loggerLevel = isLogActivated ? Level.ALL : Level.OFF;

			File istarml = new File(args[0]);
			String output = args[1];
			if (output.equals("-")) {
				BufferedReader reader = new BufferedReader(new FileReader(
						istarml));
				String content = "";
				String line;
				while ((line = reader.readLine()) != null) {
					content += line + "\n";
				}
				reader.close();
				System.out.println(transformer.IStarML2InnerXml(content));
			} else {
				File innerXml = new File(output);
				transformer.IStarML2InnerXml(istarml, innerXml);
			}
		} else {
			System.out.println("Args: <input> <output> [-log]");
			System.out.println("Args: <input> - [-log]");
			System.out.println();
			System.out.println("input: iStarML input path");
			System.out.println("output: XML output path");
			System.out.println("-log: activate logging on error stream");
		}
	}
}