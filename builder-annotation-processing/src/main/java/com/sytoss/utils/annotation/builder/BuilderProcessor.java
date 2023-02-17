package com.sytoss.utils.annotation.builder;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("com.sytoss.utils.annotation.builder.BuilderProperty")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedFieldElements = roundEnv.getElementsAnnotatedWith(annotation).stream().filter(item ->
                    item.getKind().equals(ElementKind.FIELD)).collect(Collectors.toSet());

            Set<? extends Element> annotatedClassElements = roundEnv.getElementsAnnotatedWith(annotation).stream().filter(item ->
                    item.getKind().equals(ElementKind.CLASS)).collect(Collectors.toSet());


            Map<String, BuilderMetadata> input = new HashMap<>();
            annotatedFieldElements.stream().forEach(item -> {
                TypeMirror clazz = item.getEnclosingElement().asType();
                String className = clazz.toString();
                if (!input.containsKey(className)) {
                    String superClassName = processingEnv.getTypeUtils().directSupertypes(clazz).get(0).toString();
                    input.put(className, new BuilderMetadata(className, superClassName));
                }
                String name = item.getSimpleName().toString();
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                String type = item.asType().toString();
                input.get(className).getMethods().put(name, type);
            });

            annotatedClassElements.forEach(itemClass -> {
                itemClass.getEnclosedElements()
                        .stream()
                        .filter(item -> item.getKind().equals(ElementKind.FIELD))
                        .forEach(item -> {
                            TypeMirror clazz = item.getEnclosingElement().asType();
                            String className = clazz.toString();
                            if (!input.containsKey(className)) {
                                String superClassName = processingEnv.getTypeUtils().directSupertypes(clazz).get(0).toString();
                                input.put(className, new BuilderMetadata(className, superClassName));
                            }
                            String name = item.getSimpleName().toString();
                            name = name.substring(0, 1).toUpperCase() + name.substring(1);
                            String type = item.asType().toString();
                            input.get(className).getMethods().put(name, type);
                        });
            });

            input.forEach((key, value) -> {
                try {
                    writeBuilderFile(value, input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }

        return true;
    }

    private void writeBuilderFile(BuilderMetadata metadata, Map<String, BuilderMetadata> input) throws IOException {
        BuilderMetadata superClassMetadata = input.get(metadata.getSuperClassName());
        String className = metadata.getClassName();
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("public class ");
            out.print(builderSimpleClassName);

            if (superClassMetadata != null) {
                out.print(" extends ");
                out.print(metadata.getSuperClassName());
                out.print("Builder");
            }
            out.println(" {");
            out.println();

            out.print("    private ");
            out.print(simpleClassName);
            out.print(" object = new ");
            out.print(simpleClassName);
            out.println("();");
            out.println();

            out.print("    public ");
            out.print(simpleClassName);
            out.println(" build() {");
            out.println("        return object;");
            out.println("    }");
            out.println();

            if (superClassMetadata != null) {
                out.println("    @Override ");
            }
            out.print("    protected ");
            out.print(simpleClassName);
            out.println(" getObject() {");
            out.println("        return object;");
            out.println("    }");
            out.println();

            metadata.getMethods().entrySet().forEach(setter -> {
                String builderMethodName = "with" + setter.getKey();
                String methodName = "set" + setter.getKey();
                String argumentType = setter.getValue();

                out.print("    public ");
                out.print(builderSimpleClassName);
                out.print(" ");
                out.print(builderMethodName);

                out.print("(");

                out.print(argumentType);
                out.println(" value) {");
                out.print("        getObject().");
                out.print(methodName);
                out.println("(value);");
                out.println("        return this;");
                out.println("    }");
                out.println();
            });

            while (superClassMetadata != null) {
                superClassMetadata.getMethods().entrySet().forEach(setter -> {
                    String builderMethodName = "with" + setter.getKey();
                    String argumentType = setter.getValue();

                    out.println("    @Override ");
                    out.print("    public ");
                    out.print(builderSimpleClassName);
                    out.print(" ");
                    out.print(builderMethodName);

                    out.print("(");

                    out.print(argumentType);
                    out.println(" value) {");
                    out.print("        return (");
                    out.print(builderSimpleClassName);
                    out.print(") super.");
                    out.print(builderMethodName);
                    out.println("(value);");
                    out.println("    }");
                    out.println();
                });
                superClassMetadata = input.get(superClassMetadata.getSuperClassName());
            }
            out.println("}");
        }
    }

}
