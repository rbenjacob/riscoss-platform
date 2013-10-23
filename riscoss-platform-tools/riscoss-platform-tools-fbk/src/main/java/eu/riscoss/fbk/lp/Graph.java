package eu.riscoss.fbk.lp;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.MissingResourceException;

public class Graph {

    /**
     * list of all the nodes in the graph
     */
    LinkedList<Node> nodes;

    /**
     * List of the relations in the graph.
     */
    LinkedList<Relation> relations;

    /**
     * Type of the graph, namely, with discrete or continuous propagation
     */
    String graphType;

    /**
     * Essential constructor, it only creates empty lists.
     */
    Graph() {
        this( "D" );  // the graph is discretely propagated by default
    }

    public Graph( String type ) {
        nodes     = new LinkedList<Node>();
        relations = new LinkedList<Relation>();
        graphType = type;
    }
    
    public Iterable<Relation> relations()
    {
    	return relations;
    }
    
    public Iterable<Node> nodes()
    {
    	return nodes;
    }

   /**
     * Gives the number of nodes in the graph.
     * @return the total number of nodes in the graph.
     */
    int getNumberOfNodes() {
        return nodes.size();
    }


    /**
     * adds a goal node to the list
     */
    public void addNode(Node aNode) {
        nodes.add(aNode);
    };


    /**
     * adds a relation to the list
     */
    public void addRelation(Relation aRelation) {
        relations.add(aRelation);
    };


    /**
     * during the parsing we need to match strings in the file with Labels
     * depending on whether the propagation is discrete or continuous
     */

    Label getLabel(String type)
    throws MissingResourceException {
    	if (graphType.compareTo("C") == 0)
            return Label.NO;
        else throw new MissingResourceException
            ("specified graph type is unknown", getClass().getName(), type);
    }


    /**
     * implements the propagation of the graph.
     * First, a boolean flag is set to false, indicating that there has been
     * no change in the node labels in the graph. Then, until there is
     * no change again in the node labels, we iterate on all the nodes in the
     * graph, obtaining a set of degrees of satisfaction and a set of degrees
     * of negation (as sets of labels) on which we calculate the maximum as
     * ...
     */
    public int propagate() {
        boolean debug = false;
        int repetitions = 0;
        // take the min label in order to calculate the maximums
        final Label minLabel = getLabel("MINIMUM");
        // a boolean flag that records if the labels of the nodes
        // change during the cycle (only if they do we start with
        // another cycle)
        boolean graphChanged;
        do {
            ++repetitions;
            if (debug)
                System.out.println("INIZIO CICLO");
            // sync all node labels (that is, write the current value
            // on both fields)
            for (Iterator<Node> nodeIt = nodes.iterator(); nodeIt.hasNext();)
                ((Node)nodeIt.next()).syncLabels(); // controllato, funziona
            // reset change flag
            graphChanged = false;
            // iterate again on each node in the graph
            for (Iterator<Node> nodeIt = nodes.iterator(); nodeIt.hasNext();) {
                // get the current node for quick referencing
                Node currentNode = (Node)nodeIt.next();
//                if (debug)
//                    System.out.println("SatLabel entering: " +
//                                       currentNode.getSatLabel().getName() +
//                                       "   DenLabel entering: " +
//                                       currentNode.getDenLabel().getName());
                // if we have no relations we pass to the next node
                if (currentNode.getParenthood().isEmpty()) continue;
                // since we have to calculate the max sat label and the max
                // den label, we initialize two variables to the min value
                Label maxSatisfaction = minLabel;
                Label maxNegation     = minLabel; // controllato
                // now we cycle on relations
                // parenthood e` privato, fare metodo di accesso!!
                for (Iterator<Relation> relationIt = currentNode.getParenthood().iterator();
                     relationIt.hasNext();) {
                    Relation currentRelation = (Relation)relationIt.next();
                    Label currentSatisfaction = currentRelation.solveForS();
                    Label currentNegation     = currentRelation.solveForD();
//                    if (debug)
//                        System.out.println("Current satisfaction: " +
//                                           currentSatisfaction.getName() +
//                                           "   Current negation: " +
//                                           currentNegation.getName());
                    if (currentSatisfaction.isGreaterThan(maxSatisfaction))
                        maxSatisfaction = currentSatisfaction;
                    if (currentNegation.isGreaterThan(maxNegation))
                        maxNegation = currentNegation;
                }
                // da qui
                Label currentSatisfaction = currentNode.getSatLabel();
                Label currentNegation = currentNode.getDenLabel();
                maxSatisfaction =
                    maxSatisfaction.isGreaterThan(currentSatisfaction) ?
                    maxSatisfaction : currentSatisfaction;
                maxNegation =
                    maxNegation.isGreaterThan(currentNegation) ?
                    maxNegation : currentNegation;
                // a qui, le righe aggiunte
                // now we set the values
                currentNode.setSatLabel(maxSatisfaction);
                currentNode.setDenLabel(maxNegation);
//                if (debug)
//                    System.out.println("SatLabel exiting: " +
//                                       currentNode.getSatLabel().getName() +
//                                       "   DenLabel exiting: " +
//                                       currentNode.getDenLabel().getName());
                if (currentNode.hasChanged()) graphChanged = true;
            }

        }
        while (graphChanged == true);
        return repetitions;
    };
}
