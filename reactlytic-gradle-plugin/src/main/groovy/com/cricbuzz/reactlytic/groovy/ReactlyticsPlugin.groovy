package com.cricbuzz.reactlytic.groovy

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.compile.GroovyCompile

/**
 * Created by sathish-n on 21/6/16.
 */
class ReactlyticsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {

            project.android.applicationVariants.all { variant ->

            File inputWordFile = new File(project.projectDir.getParentFile(), "reactviewlist.json")
            File outputDir = new File(project.buildDir, "/intermediates/assets/${variant.dirName}")
            // exclude source and sourcemap from release builds
            def copyJsonSourceTask = project.tasks.create(name:"copy${variant.name}JsonSource", type: Copy) {
                from(inputWordFile)
                into(outputDir)
            }

            def delJsonSourceTask = project.tasks.create(name:"del${variant.name}JsonSource", type: Delete) {
                delete inputWordFile
            }

            copyJsonSourceTask.finalizedBy(delJsonSourceTask)

            Set collections = project.getTasksByName("compile${variant.name.capitalize()}JavaWithJavac", false);
            Iterator iterator = collections.iterator();
            while (iterator.hasNext()) {
                Task element = (Task) iterator.next();
                println(element.name)
                if (element.name.contains('compile')) {
                    element.finalizedBy(copyJsonSourceTask)
                }
            }
        }

//        project.afterEvaluate {
//            project.android.applicationVariants.all { variant ->
//                File inputWordFile = new File(project.projectDir.getParentFile(), "reactviewlist.json")
//                File outputDir = new File(project.buildDir, "generated/source/apt/${variant.dirName}")
//                println(variant.name.capitalize())
//                def task = project.tasks.create(name: "analyticsConvertJsonToJava${variant.name.capitalize()}", type: BasicDeviceListTask) {
//                    outDir = outputDir
//                    wordsFile = inputWordFile
//                }
//
////                project.configurations.all {
////                    it.properties
////                    println("configurations " + it.properties)
////
////                }
////                project.configurations.compile.resolve().each { println it }
////                project.configurations.runtime.resolve().each { println it }
////                project.android.sourceSets.all {
////                    println("Sourcesets " + it)
////                }
//                addGeneratedToSource(project, variant);
//                Set collections = project.getTasksByName("compile${variant.name.capitalize()}JavaWithJavac", false);
//                Iterator iterator = collections.iterator();
//                while (iterator.hasNext()) {
//                    Task element = (Task) iterator.next();
//                    println(element.name)
//                    if (element.name.contains('compile')) {
//                        element.finalizedBy(task)
//                    }
//                }
//
//            }
//
//            project.android.sourceSets.all {
//                    println(" New Sourcesets " + it)
//                }
//
////                if (project.extensions.apt.disableDiscovery() && !project.extensions.apt.processors()) {
////                    throw new ProjectConfigurationException('android-apt configuration error: disableDiscovery may only be enabled in the apt configuration when there\'s at least one processor configured', null);
////                }
//
////                project.android.applicationVariants.all { appVariant ->
////
////                    Configuration aptConfiguration = project.configurations.getByName('apt');
////                    def aptTestConfiguration = null;
////
////                    try {
////                        aptTestConfiguration = project.configurations.getByName('androidTestApt')
////                    } catch (UnknownConfigurationException ex) {
////                        // this can be missing in the case of the com.android.test plugin
////                    }
////
////                    def aptUnitTestConfiguration
////                    try {
////                        aptUnitTestConfiguration = project.configurations.getByName('testApt')
////                    } catch (UnknownConfigurationException ex) {
////                        // missing on older plugin version
////                    }
////
////                    ReactlyticsPlugin.configureVariant(project, appVariant, aptConfiguration, project.extensions.apt)
////                    if (appVariant.testVariant && aptTestConfiguration) {
////                        ReactlyticsPlugin.configureVariant(project, appVariant.testVariant, aptTestConfiguration, project.extensions.apt)
////                    }
////                    if (appVariant.hasProperty("unitTestVariant") && aptUnitTestConfiguration) {
////                        ReactlyticsPlugin.configureVariant(project, appVariant.unitTestVariant, aptUnitTestConfiguration, project.extensions.apt)
////                    }
////                }
//
////            target.rootProject.evaluationDependsOn("/build/generated/source/apt/${variant.dirName}")
//
////            target.sourceSets.main.java.srcDirs += [target.buildDir.path+"generated/source/apt/${variant.dirName}"]
////            println(target.sourceSets)
//
////            variant.registerJavaGeneratingTask task, outputDir
////            , mustRunAfter: "compile${variant.name.capitalize()}"+"Sources"
////            task.mustRunAfter("compile${variant.name.capitalize()}"+"Sources")
//        }
    }

    void addGeneratedToSource(Project project, def variant) {

//        project.sourceSets.all {
//            println("sourcesets " + it.asPath)
//        }
//        def aptOutputDir = project.file(new File(project.buildDir, "generated/source/apt"))
//        def aptOutput = new File(aptOutputDir, variant.dirName)
//        def javaCompile = variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile
////        javaCompile.classpath.add("${project.buildDir}${File.separator}generated${File.separator}src${File.separator}apt${File.separator}${variant.dirName}")
//        variant.addJavaSourceFoldersToModel(aptOutput);
//
//        println("java Compile " + javaCompile.classpath.asPath)
        project.android.sourceSets.matching { it.name == "main"||it.name == "debug"||it.name == "release" }.all {
            println("sourcesets " + it)
            it.java.srcDir "${project.buildDir}${File.separator}generated${File.separator}source${File.separator}apt${File.separator}${variant.dirName}"

        }
    }


    static void configureVariant(
            def project,
            def variant, def aptConfiguration, def aptExtension) {
        if (aptConfiguration.empty) {
            project.logger.info("No apt dependencies for configuration ${aptConfiguration.name}");
            return;
        }

        def aptOutputDir = project.file(new File(project.buildDir, "generated/source/apt"))
        def aptOutput = new File(aptOutputDir, variant.dirName)

        def javaCompile = variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile

        variant.addJavaSourceFoldersToModel(aptOutput);
        def processorPath = (aptConfiguration + javaCompile.classpath).asPath
        def taskDependency = aptConfiguration.buildDependencies
        if (taskDependency) {
            javaCompile.dependsOn += taskDependency
        }

//        def processors = aptExtension.processors()

        javaCompile.options.compilerArgs += [
                '-s', aptOutput
        ]

//        if (processors) {
//            javaCompile.options.compilerArgs += [
//                    '-processor', processors
//            ]
//        }

//        if (!(processors && aptExtension.disableDiscovery())) {
        javaCompile.options.compilerArgs += [
                '-processorpath', processorPath
        ]
//        }

        aptExtension.aptArguments.variant = variant
        aptExtension.aptArguments.project = project
        aptExtension.aptArguments.android = project.android

        javaCompile.options.compilerArgs += aptExtension.arguments()

        javaCompile.doFirst {
            aptOutput.mkdirs()
        }

        // Groovy compilation is added by the groovy-android-gradle-plugin in finalizedBy
        def dependency = javaCompile.finalizedBy;
        def dependencies = dependency.getDependencies(javaCompile);
        for (def dep : dependencies) {
            if (dep instanceof GroovyCompile) {
                if (dep.groovyOptions.hasProperty("javaAnnotationProcessing")) {
                    dep.options.compilerArgs += javaCompile.options.compilerArgs;
                    dep.groovyOptions.javaAnnotationProcessing = true
                }
            }
        }
    }


}
