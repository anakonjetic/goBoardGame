package hr.tvz.konjetic.goboardgame.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocumentationUtils {

    public static void generateDocumentation() {
        StringBuilder documentationGenerator  = new StringBuilder();

        //String path = "C:\\Users\\Korisnik\\IdeaProjects\\goBoardGame\\src\\main\\java\\hr\\tvz\\konjetic\\goboardgame";
        // fqcn - fully qualified class name

        String path = "C:\\Users\\Korisnik\\IdeaProjects\\goBoardGame\\src\\main\\java";

        try {
            List<Path> classNameList =  Files.walk(Paths.get(path))
                    .filter(p -> p.getFileName().toString().endsWith(".java"))
                    .filter(p -> !p.getFileName().toString().equals("module-info.java"))
                    .toList();

            // Class.forName("java.hr.tvz.konjetic.goboardgame.GameState")

            for (Path classPath : classNameList){

                //u main.java.hr.tvz.... mi nađemo index od hr i krećemo substring of njega
                int indexOfHr = classPath.toString().indexOf("hr");
                String fqcn =  classPath.toString().substring(indexOfHr);

                //sad mijenjamo \ sa .
                fqcn = fqcn.replace('\\', '.');

                //sad mičemo .java s kraja
                fqcn = fqcn.substring(0, fqcn.length()-5);


                //sad dobivamo tu klasu kao iz imena
                Class<?> documentationClass = Class.forName(fqcn);

                String classModifiers = Modifier.toString(documentationClass.getModifiers());

                Field[] classVariables = documentationClass.getFields();

                documentationGenerator.append("<h2>"
                        + classModifiers
                        + fqcn + "\n" +
                        "</h2>\n");

                for(Field field : classVariables){
                    String modifiers = Modifier.toString(field.getModifiers());

                    documentationGenerator.append("<h3>"
                            + modifiers + " " +
                            field.getType().getName() + " "
                            + field.getName() +
                            "</h3>\n");
                }

                Constructor[] classConstructors = documentationClass.getConstructors();


            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                <title>Documentation</title>
                </head>
                <body>
                <h1>List of classes</h1>
                """
                + documentationGenerator.toString() +
                """
                </body>
                </html>
                """;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("documentation/doc.html"))){
            writer.write(html);
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
