package net.elehack.maven.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tear down the out-of-tree build directory.
 */
@Mojo(name="teardown-build-dir",
      requiresDependencyResolution = ResolutionScope.NONE,
      threadSafe = false,
      defaultPhase = LifecyclePhase.CLEAN)
public class TeardownBuildDirMojo extends AbstractMojo
{
    /**
     * The target build directory.
     */
    @Parameter(property="project.build.directory", required=true)
    private File targetDirectory;

    @Override
    public void execute() throws MojoExecutionException
    {
        Path target = targetDirectory.toPath();
        if (Files.isSymbolicLink(target)) {
            try {
                getLog().info("deleting symlink " + target.toString());
                Files.delete(target);
            } catch (IOException e) {
                getLog().error("error deleting " + target.toString(), e);
            }
        }
    }
}
