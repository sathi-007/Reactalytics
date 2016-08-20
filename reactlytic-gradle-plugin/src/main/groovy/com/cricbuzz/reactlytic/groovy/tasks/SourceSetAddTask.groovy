package com.cricbuzz.reactlytic.groovy.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction

/**
 * Created by sathish-n on 23/6/16.
 */
class SourceSetAddTask extends DefaultTask {

    Project project

    @TaskAction
    def printDevicesList() {
        final SourceSetContainer sourceSets = project.convention.getPlugin(JavaPluginConvention).sourceSets;
        SourceSet sourceSet = sourceSets.findByName('main');
//            sourceSet.compileClasspath =
        println(sourceSets)
        project.android.applicationVariants.all { variant ->
            sourceSet.java.srcDirs += [project.buildDir.path + "/generated/source/apt/${variant.dirName}"]
        }

        println(sourceSet.java.srcDirs)
    }
}
