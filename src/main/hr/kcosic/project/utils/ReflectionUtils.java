package main.hr.kcosic.project.utils;

import javax.swing.text.html.HTMLDocument;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class ReflectionUtils {

    public static void readClassInfo(Class<?> clazz, StringBuilder classInfo) {
        classInfo.append("<h4>");
        appendPackage(clazz, classInfo);
        classInfo.append("</h4>")
                .append("<h3>");
        appendModifiers(clazz, classInfo);
        classInfo.append("</h3>")
                .append("<h5>");
        appendParent(clazz, classInfo, true);
        classInfo.append("<br/>");
        appendInterfaces(clazz, classInfo);
        classInfo.append("</h5>");
    }

    private static void appendPackage(Class<?> clazz, StringBuilder classInfo) {
        classInfo.append("<span style=\"color:orange\">")
                .append(clazz.getPackage())
                .append("</span>");
    }

    private static void appendModifiers(Class<?> clazz, StringBuilder classInfo) {

        classInfo.append("<span style=\"color:#0000b3\">")
                .append(Modifier.toString(clazz.getModifiers()))
                .append("</span>")
                .append("&nbsp;")
                .append("<span style=\"color:#2fbd2f\">")
                .append(clazz.getSimpleName())
                .append("</span>");
    }

    private static void appendParent(Class<?> clazz, StringBuilder classInfo, boolean first) {
        Class<?> parent = clazz.getSuperclass();
        if (parent == null) {
            return;
        }
        if (first) {
            classInfo.append("<br/><span style=\"color:#0000b3\">extends</span><br/>");
        }
        classInfo.append("<span style=\"color:#2fbd2f\">")
                .append(" ")
                .append(checkClassName(parent.getName()))
                .append("</span>");
        appendParent(parent, classInfo, false);
    }

    private static void appendInterfaces(Class<?> clazz, StringBuilder classInfo) {
        if (clazz.getInterfaces().length > 0) {
            classInfo.append("<br/><span style=\"color:#0000b3\">implements</span><br/>");
        }
        for (Class<?> in : clazz.getInterfaces()) {
            classInfo.append("<span style=\"color:#2fbd2f\">")
                    .append(" ")
                    .append(checkClassName(in.getName()))
                    .append("</span>");
        }
    }

    public static void readClassAndMembersInfo(Class<?> clazz, StringBuilder classAndMembersInfo) {
        readClassInfo(clazz, classAndMembersInfo);
        appendFields(clazz, classAndMembersInfo);
        appendMethods(clazz, classAndMembersInfo);
        appendConstructors(clazz, classAndMembersInfo);
    }

    private static void appendFields(Class<?> clazz, StringBuilder classAndMembersInfo) {
        //Field[] fields = clazz.getFields(); // returns public and inherited
        classAndMembersInfo.append("Fields:");
        Field[] fields = clazz.getDeclaredFields(); // returns public, protected, default (package) access, and private fields, but excludes inherited fields
        classAndMembersInfo.append("<ul>");
        for (Field field : fields) {
            classAndMembersInfo
                    .append("<li>");

            if(field.getDeclaredAnnotations().length > 0){
                for (var annotation :
                        field.getDeclaredAnnotations()) {
                    classAndMembersInfo.append("<span style=\"color:#c1ad00\">")
                            .append(annotation)
                            .append("</span>")
                            .append("<br/>");
                }
            }
            classAndMembersInfo
                    .append("<span style=\"color:#0000b3\">&nbsp;")
                    .append(Modifier.toString(field.getModifiers()))
                    .append("</span>")

                    .append("<span style=\"color:#2fbd2f\">&nbsp;")
                    .append(checkClassName(field.getType().getName()))//type of property
                    .append("</span>")

                    .append("<span style=\"color:#4ac1e8\">&nbsp;")
                    .append(field.getName())//name of property
                    .append("</span>")
                    .append("</li>");
        }
        classAndMembersInfo.append("</ul>");

    }

    private static String checkClassName(String name) {
        if(name.contains("main.hr.kcosic.project")){
            return "<a style=\"color:unset\" href=\"./"+name.substring(name.lastIndexOf('.') + 1)+".html\">" + name + "</a>";
        }
        return name;
    }

    private static void appendMethods(Class<?> clazz, StringBuilder classAndMembersInfo) {
        classAndMembersInfo
                .append("Methods:")
                .append("<ul>");
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {

            classAndMembersInfo.append("<li>");
            appendMethodAnnotations(method, classAndMembersInfo);
            classAndMembersInfo
                    .append("<span style=\"color:#0000b3\">")
                    .append(Modifier.toString(method.getModifiers()))
                    .append("</span>&nbsp;")

                    .append("<span style=\"color:#2fbd2f\">")
                    .append(method.getReturnType())
                    .append("</span>&nbsp;")
                    .append("<span style=\"color:#c1ad00\">")
                    .append(method.getName())
                    .append("</span>&nbsp;");

            appendParameters(method, classAndMembersInfo);
            appendExceptions(method, classAndMembersInfo);
            classAndMembersInfo.append("</li>");

        }
        classAndMembersInfo.append("</ul>");
    }

    private static void appendMethodAnnotations(Executable executable, StringBuilder classAndMembersInfo) {
        for (Annotation annotation : executable.getAnnotations()) {

            classAndMembersInfo.append("<span style=\"color: #c1ad00\">")
                    .append(annotation)
                    .append("</span><br/>");
        }
    }

    private static void appendParameters(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append("(");
        for (Parameter parameter : executable.getParameters()) {
            classAndMembersInfo
                    .append("<span style=\"color: #2fbd2f\">")
                    .append(parameter.getType().getName())
                    .append("</span>&nbsp;")
                    .append("<span style=\"color: #4ac1e8\">")
                    .append(parameter.getName())
                    .append("</span>")
                    .append(",&nbsp;");
        }
        if (classAndMembersInfo.toString().endsWith(", ")) {
            classAndMembersInfo.delete(classAndMembersInfo.length() - 2, classAndMembersInfo.length());
        }
        classAndMembersInfo.append(")");
    }

    private static void appendExceptions(Executable executable, StringBuilder classAndMembersInfo) {
        Class<?>[] exceptionTypes = executable.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            classAndMembersInfo.append("&nbsp;<br/><span style=\"color:#e86ce8\">throws</span><ul>");
            for (Class<?> exceptionType : exceptionTypes) {
                classAndMembersInfo.append("<li><span style=\"color:#e86ce8\">")
                        .append(exceptionType)
                        .append("</span></li>");
            }
            if (classAndMembersInfo.toString().endsWith(", ")) {
                classAndMembersInfo.delete(classAndMembersInfo.length() - 2, classAndMembersInfo.length());
            }
            classAndMembersInfo.append("</ul>");
        }
    }

    private static void appendConstructors(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        classAndMembersInfo.append("Constructors:<br/>");
        for (Constructor constructor : constructors) {
            appendMethodAnnotations(constructor, classAndMembersInfo);
            classAndMembersInfo
                    .append("<span style=\"color: #0000b3\"")
                    .append(Modifier.toString(constructor.getModifiers()))
                    .append("</span>&nbsp;")
                    .append("<span style=\"color: #2fbd2f\">")
                    .append(constructor.getName())
                    .append("</span>&nbsp;");
            appendParameters(constructor, classAndMembersInfo);
            appendExceptions(constructor, classAndMembersInfo);
            classAndMembersInfo.append("<br/>");
        }
    }

}
