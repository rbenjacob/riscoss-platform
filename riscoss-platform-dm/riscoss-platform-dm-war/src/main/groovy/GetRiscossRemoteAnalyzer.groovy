/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 2 -*- */
import java.nio.file.Files;
import java.nio.file.Paths;

String input = project.properties["indir"] + "/" + project.properties["filename"];
String output = project.properties["outfile"];
System.out.println("\n\nCopying " + input + " --> " + output);
Files.copy(Paths.get(input), Paths.get(output));

