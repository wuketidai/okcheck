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

package com.liulishuo.okcheck.util

import org.gradle.api.Project

class ChangeModule {

    static List<String> getAllModuleList(Project project) {
        final List<String> moduleNameList = new ArrayList<>()

        if (project.subprojects.size() <= 0) {
            moduleNameList.add(project.name)
        } else {
            project.subprojects {
                moduleNameList.add(it.name)
            }
        }

        return moduleNameList
    }

    static List<String> getChangedModuleList(Project project, List<String> changedFilePaths) {
        final List<PathModule> pathModuleList = new ArrayList<>()
        File rootDir = project.rootProject.rootDir
        if (project.subprojects.size() <= 0) {
            String relativePath = rootDir.toURI().relativize(project.projectDir.toURI()).getPath()
            pathModuleList.add(new PathModule(relativePath, project.name))
        } else {
            project.subprojects {
                String relativePath = rootDir.toURI().relativize(it.projectDir.toURI()).getPath()
                pathModuleList.add(new PathModule(relativePath, it.name))
            }
        }

        final List<String> changedModuleNameList = new ArrayList<>()
        final Map<String,List<String>> changeModuleMap = new HashMap<>()
        changedFilePaths.forEach {

            for (int i = 0; i < pathModuleList.size(); i++) {
                PathModule pathModule = pathModuleList.get(i)
                if (!changedModuleNameList.contains(pathModule.moduleName)
                        && pathModule.isOnThisModule(it)) {
                    changedModuleNameList.add(pathModule.moduleName)
                }

                List<String> fileList = changeModuleMap.get(pathModule.moduleName)
                if (fileList == null) {
                    fileList = new ArrayList<>()
                }
                fileList.add(it)
                changeModuleMap.put(pathModule.moduleName,fileList)
            }
        }

        IncrementFilesHelper.instance.addModuleChangeFile(changeModuleMap)
        return changedModuleNameList
    }

    private static class PathModule {
        final String relativePath
        final String moduleName

        PathModule(String relativePath, String moduleName) {
            this.relativePath = relativePath
            this.moduleName = moduleName
        }

        boolean isOnThisModule(String fileRelativePath) {
            return fileRelativePath.startsWith(this.relativePath)
        }
    }
}
