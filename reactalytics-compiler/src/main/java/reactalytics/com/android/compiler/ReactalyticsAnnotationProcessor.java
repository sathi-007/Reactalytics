package reactalytics.com.android.compiler;

import com.cricbuzz.android.reactalytics.annotations.*;
import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.ElementKind.METHOD;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

@AutoService(Processor.class)
public final class ReactalyticsAnnotationProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;
    private static final String ANNOTATION = "@" + Reactalytics.class.getSimpleName();
    private static final String BINDING_CLASS_SUFFIX = "$$ViewBinder";
    static final String VIEW_TYPE = "android.app.Activity";
    static final String GEN_CLASS_NAME = "Reactlytics" + BINDING_CLASS_SUFFIX;
    static final String packageName = "reactalytics.cricbuzz.com.lib";

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        messager = env.getMessager();
        elementUtils = env.getElementUtils();
        typeUtils = env.getTypeUtils();
        filer = env.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        messager.printMessage(NOTE, "Reactalytics.class.getCanonicalName()");
        Set<String> types = new LinkedHashSet<>();

        types.add(Reactalytics.class.getCanonicalName());
        types.add(TrackEvent.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Map<TypeElement, BindClass> targetBindClassMap = new LinkedHashMap<>();

        messager.printMessage(NOTE, "Reactlytics annotation is being processed");
        for (Element element : roundEnv.getElementsAnnotatedWith(Reactalytics.class)) {

            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseReactalytics(element, targetBindClassMap);
            } catch (Exception e) {
                logParsingError(element, Reactalytics.class, e);
            }
        }

        messager.printMessage(NOTE, "TrackEvent annotation is being processed");
        for (Element element : roundEnv.getElementsAnnotatedWith(TrackEvent.class)) {
            if (!SuperficialValidation.validateElement(element)) continue;
            try {
                parseTrackEvent(element, targetBindClassMap);
            } catch (Exception e) {
                logParsingError(element, TrackEvent.class, e);
            }
        }

        if (targetBindClassMap.size() > 0) {

            try {
                messager.printMessage(NOTE, GEN_CLASS_NAME + " is being generated");
                brewJava(targetBindClassMap);

            } catch (IOException e) {
                TypeElement typeElement = elementUtils.getTypeElement(GEN_CLASS_NAME);
                error(typeElement, "Unable to write view binder for type %s: %s", typeElement,
                        e.getMessage());
            }
        }

        return true;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        messager.printMessage(NOTE, GEN_CLASS_NAME + " Completion annotation " + s);
        return super.getCompletions(element, annotationMirror, executableElement, s);
    }

    private void parseReactalytics(Element element, Map<TypeElement, BindClass> targetClassMap) {
        TypeElement annotationElement = (TypeElement) element;
        if (!isValidClass(annotationElement)) {
            return;
        }
        BindClass bindingClass = getOrCreateTargetClass(targetClassMap, annotationElement);
        String[] trackers = element.getAnnotation(Reactalytics.class).value();
        for (String tracker : trackers) {
            bindingClass.addTrackerFilter(tracker);
        }
//        String packageName = getPackageName(annotationElement);
//        String className = getClassName(annotationElement,packageName);
        String screenName = element.getAnnotation(Reactalytics.class).screenName();
        bindingClass.setScreenName(screenName);
        bindingClass.setClassName(annotationElement.getQualifiedName().toString());
    }

    private void parseTrackEvent(Element element, Map<TypeElement, BindClass> targetClassMap) {
        TypeElement annotationElement = (TypeElement) element.getEnclosingElement();
        if (!isValidClass(annotationElement)) {
            return;
        }

        if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a method.", TrackEvent.class.getSimpleName()));
        }

        ExecutableElement executableElement = (ExecutableElement) element;

        String name = executableElement.getSimpleName().toString();

        BindClass bindingClass = getOrCreateTargetClass(targetClassMap, annotationElement);
        if (bindingClass != null) {
            messager.printMessage(NOTE, "Activity Method Name " + name);
            bindingClass.addMethodName(name);
        }
    }


    private BindClass getOrCreateTargetClass(Map<TypeElement, BindClass> targetClassMap,
                                             TypeElement annotationElement) {
        BindClass bindingClass = targetClassMap.get(annotationElement);
        if (bindingClass == null) {
            String packageName = getPackageName(annotationElement);

            bindingClass = new BindClass(getClassName(annotationElement, packageName));
            targetClassMap.put(annotationElement, bindingClass);
        }
        return bindingClass;
    }


    private void brewJava(Map<TypeElement, BindClass> targetBindClassMap) throws IOException {
        Gson gson = new Gson();
        PrintWriter pw = new PrintWriter(new FileOutputStream("./reactviewlist.json", false));
        List<BindClass> bindClasses = new ArrayList<>();
        for (Map.Entry<TypeElement, BindClass> entry : targetBindClassMap.entrySet()) {
            BindClass bindingClass = entry.getValue();
            bindClasses.add(bindingClass);
        }
        MainBinderClass bindingClass = new MainBinderClass();
        bindingClass.setBindClassList(bindClasses);
        pw.println(gson.toJson(bindingClass));
        pw.close();
    }

    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        String className = type.getQualifiedName().toString().substring(packageLen).replace(".", "");
        messager.printMessage(NOTE, "Activity Class type Name " + className);
        return className;
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }

    private void logParsingError(Element element, Class<? extends Annotation> annotation,
                                 Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        error(element, "Unable to parse @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
    }

    private boolean isValidClass(TypeElement annotatedClass) {

        if (!ClassValidator.isPublic(annotatedClass)) {
            String message = String.format("Classes annotated with %s must be public.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        if (ClassValidator.isAbstract(annotatedClass)) {
            String message = String.format("Classes annotated with %s must not be abstract.",
                    ANNOTATION);
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        return true;
    }
}
