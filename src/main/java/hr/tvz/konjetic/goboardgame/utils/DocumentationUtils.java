package hr.tvz.konjetic.goboardgame.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocumentationUtils {


    public static void generateDocumentation() {
        StringBuilder documentationGenerator = new StringBuilder();

        String path = "C:\\Users\\Korisnik\\IdeaProjects\\goBoardGame\\src\\main\\java";

        try {
            List<Path> classNameList = Files.walk(Paths.get(path))
                    .filter(p -> p.getFileName().toString().endsWith(".java"))
                    .filter(p -> !p.getFileName().toString().equals("module-info.java"))
                    .toList();

            for (Path classPath : classNameList) {
                int indexOfHr = classPath.toString().indexOf("hr");
                String fqcn = classPath.toString().substring(indexOfHr);

                fqcn = fqcn.replace('\\', '.');
                fqcn = fqcn.substring(0, fqcn.length() - 5);

                Class<?> documentationClass = Class.forName(fqcn);
                String classModifiers = Modifier.toString(documentationClass.getModifiers());
                documentationGenerator.append("<h2>")
                        .append(classModifiers)
                        .append(" ")
                        .append(fqcn)
                        .append("</h2>\n");

                Field[] classVariables = documentationClass.getDeclaredFields();
                if (classVariables.length > 0) {
                    documentationGenerator.append("<h3>Fields:</h3>\n");
                    for (Field field : classVariables) {
                        String modifiers = Modifier.toString(field.getModifiers());
                        documentationGenerator.append("<p>")
                                .append(modifiers)
                                .append(" ")
                                .append(field.getType().getName())
                                .append(" ")
                                .append(field.getName())
                                .append("</p>\n");
                    }
                }

                Constructor[] classConstructors = documentationClass.getDeclaredConstructors();
                if (classConstructors.length > 0) {
                    documentationGenerator.append("<h3>Constructors:</h3>\n");
                    for (Constructor constructor : classConstructors) {
                        String modifiers = Modifier.toString(constructor.getModifiers());
                        documentationGenerator.append("<p>")
                                .append(modifiers)
                                .append(" ")
                                .append(constructor.getName())
                                .append("(");

                        Parameter[] parameters = constructor.getParameters();
                        for (int i = 0; i < parameters.length; i++) {
                            if (i > 0) {
                                documentationGenerator.append(", ");
                            }
                            documentationGenerator.append(parameters[i].getType().getName())
                                    .append(" ")
                                    .append(parameters[i].getName());
                        }
                        documentationGenerator.append(")</p>\n");
                    }
                }

                Method[] classMethods = documentationClass.getDeclaredMethods();
                if (classMethods.length > 0) {
                    documentationGenerator.append("<h3>Methods:</h3>\n");
                    for (Method method : classMethods) {
                        String modifiers = Modifier.toString(method.getModifiers());
                        String returnType = method.getReturnType().getName();
                        String methodName = method.getName();
                        documentationGenerator.append("<p>")
                                .append(modifiers)
                                .append(" ")
                                .append(returnType)
                                .append(" ")
                                .append(methodName)
                                .append("(");

                        Parameter[] parameters = method.getParameters();
                        for (int i = 0; i < parameters.length; i++) {
                            if (i > 0) {
                                documentationGenerator.append(", ");
                            }
                            documentationGenerator.append(parameters[i].getType().getName())
                                    .append(" ")
                                    .append(parameters[i].getName());
                        }
                        documentationGenerator.append(")</p>\n");
                    }
                }
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
            <h1>GO Board Game Documentation</h1>
            """
                + documentationGenerator.toString() +
                """
                </body>
                </html>
                """;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("documentation/doc.html"))) {
            writer.write(html);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
