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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

/**
 * Set up the out-of-tree build directory.
 */
@Mojo(name="setup-build-dir",
      requiresDependencyResolution = ResolutionScope.NONE,
      threadSafe = false,
      defaultPhase = LifecyclePhase.INITIALIZE)
public class SetupBuildDirMojo extends AbstractMojo
{
    /**
     * The external build directory.
     */
    @Parameter(property="external.build.directory", required=true)
    private File buildDirectory;

    /**
     * The target build directory.
     */
    @Parameter(property="project.build.directory", required=true)
    private File targetDirectory;

    @Override
    public void execute() throws MojoExecutionException
    {
        Path target = targetDirectory.toPath();
        Path build = buildDirectory.toPath();

        if (Files.exists(target)) {
            getLog().info("target already exists, doing nothing");
            return;
        }

        getLog().info("using external build in " + build);

        try {
            if (!Files.exists(build)) {
                getLog().debug("creating " + build.toString());
                Files.createDirectories(build);
            }

            getLog().debug("deleting " + target);
            // if files exists, there's a race or it's a bad symlink. Delete!
            Files.deleteIfExists(target);

            getLog().debug("creating symlink " + target + " -> " + build);
            // and create the symlink
            Files.createSymbolicLink(target, build);
        } catch (IOException e) {
            getLog().error("error setting up target directory", e);
            throw new MojoExecutionException("error setting up target directory", e);
        }
    }
}
