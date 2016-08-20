package com.cricbuzz.reactlytic.groovy.tasks

import com.squareup.javapoet.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.lang.model.element.Modifier

/**
 * Created by sathish-n on 22/6/16.
 */
class BasicDeviceListTask extends DefaultTask {
    String group = "other"
    String description = "Runs adb devices command"
    private static final String BINDING_CLASS_SUFFIX = '$$ViewBinder';
    static final String GEN_CLASS_NAME = "Reactlytics" + BINDING_CLASS_SUFFIX;
    static final String packageName = "reactalytics.cricbuzz.com.sample.view.fragment";
    private static final ClassName AnnotatedInfo = ClassName.get("reactalytics.cricbuzz.com.lib.internal", "AnnotatedInfo");
    File wordsFile
    File outDir

    @TaskAction
    def printDevicesList() {
//        def adbExe = project.android.getAdbExe().toString()
//        println "${adbExe} devices".execute().text

//        println  project.projectDir.getParentFile().absolutePath
//        println  project.projectDir.absolutePath
//        println wordsFile.absolutePath
//        println outDir.canonicalPath

        ClassName string = ClassName.get("java.lang", "String");
        ClassName list = ClassName.get("java.util", "List");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName stringArrayListTypeName = ParameterizedTypeName.get(list, string);

        FieldSpec reactiveViewList = FieldSpec.builder(stringArrayListTypeName, "reactiveViewList")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer('new $T<>()', arrayList)
                .build();
        MethodSpec.Builder beyondBuilder = MethodSpec.methodBuilder("addReactiveViewList")
                .returns(void.class);

        wordsFile.readLines().each {
            beyondBuilder.addStatement('$N.add($S)', reactiveViewList, it);
        }

        MethodSpec beyondMethod = beyondBuilder.build();

        MethodSpec getReactiveViewList = MethodSpec.methodBuilder("getAnnotatedInfo")
                .addModifiers(Modifier.PUBLIC)
                .returns(stringArrayListTypeName)
                .addStatement('return $N', reactiveViewList)
                .build();

        MethodSpec flux = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("addReactiveViewList()")
                .build();


        ClassName classFqcn = ClassName.get(packageName,
                GEN_CLASS_NAME);

        TypeSpec.Builder result = TypeSpec.classBuilder(classFqcn)
                .addModifiers(Modifier.PUBLIC)
                .addField(reactiveViewList)
                .addMethod(beyondMethod)
                .addMethod(getReactiveViewList)
                .addMethod(flux);

        TypeName declaredSet = ParameterizedTypeName.get(AnnotatedInfo, ClassName.get("java.lang", "String"));

        result.addSuperinterface(declaredSet);

        JavaFile javaFile =  JavaFile.builder(classFqcn.packageName(), result.build())
                .addFileComment("Generated code from Reactalytics. Do not modify!")
                .build();
        javaFile.writeTo(outDir)



    }
}
