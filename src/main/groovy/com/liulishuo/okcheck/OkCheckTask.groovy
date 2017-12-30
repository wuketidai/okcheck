/*
 * Copyright (c) 2017 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liulishuo.okcheck

import com.liulishuo.okcheck.util.BuildConfig
import com.liulishuo.okcheck.util.ChangeFile
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class OkCheckTask extends DefaultTask {
    @Input
    List<String> changedModuleList

    @Input
    boolean isMock

    OkCheckTask() {
        setDescription("check project only for changed files")
    }

    @TaskAction
    void setupOkcheck() {
        if (project == project.rootProject) {
            println "OkCheck: Finish root okcheck task!"
        } else if (!isMock) {
            println "OkCheck: Finish ${project.name} okcheck task!"
            BuildConfig.addToPassedModuleFile(project)

            if (BuildConfig.isAllModulePassed(project, changedModuleList)) {
                new ChangeFile(project.rootProject.name).refreshLastExecCommitId()
                println "OkCheck: All check is passed and refreshed the commit to current one!"
            }
        }

    }

    static def addValidTask(Project project, List<String> moduleList) {
        project.task(OkCheckPlugin.TASK_NAME, type: OkCheckTask, overwrite: true) {
            dependsOn project.getTasksByName('check', false)
            changedModuleList = moduleList
            isMock = false
        }
    }

    static def addMockTask(Project project) {
        project.task(OkCheckPlugin.TASK_NAME, type: OkCheckTask, overwrite: true) {
            changedModuleList = new ArrayList<>()
            isMock = true
        }
    }
}
