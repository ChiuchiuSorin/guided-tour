/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import remapping from "@jridgewell/remapping";
import vue from "@vitejs/plugin-vue";
import { defineConfig, mergeConfig } from "vite";
import cssInjectedByJsPlugin from "vite-plugin-css-injected-by-js";
import dts from "vite-plugin-dts";
import {
  copyFileSync,
  existsSync,
  readFileSync,
  readdirSync,
  writeFileSync,
} from "node:fs";
import { basename, dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import type { UserConfig } from "vite";

function flattenSourceMaps(buildOutDir: string) {
  return {
    name: "flatten-sourcemaps",
    async closeBundle() {
      const mapFiles = readdirSync(buildOutDir, {
        recursive: true,
        withFileTypes: true,
      })
        .filter((file) => file.name.endsWith(".map"))
        .map((file) => join(file.parentPath, file.name));

      for (const mapFile of mapFiles) {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        const map = JSON.parse(readFileSync(mapFile, "utf-8")) as any;
        const flattened = remapping(map, (sourceFile: string) => {
          const absolutePath = resolve(dirname(mapFile), sourceFile);
          try {
            return JSON.parse(
              readFileSync(absolutePath + ".map", "utf-8"),
              // eslint-disable-next-line @typescript-eslint/no-explicit-any
            ) as any;
          } catch {
            return null;
          }
        });
        writeFileSync(mapFile, JSON.stringify(flattened));
      }
    },
  };
}

function generateWebjarNodeConfig(
  path: string,
  toBundle: string[] = [],
): UserConfig {
  const WEBJAR_NODE_OUT_DIR = "../../../target/node-dist";
  const __dirname = dirname(fileURLToPath(path));
  return defineConfig({
    build: {
      outDir: WEBJAR_NODE_OUT_DIR,
      lib: {
        entry: resolve(__dirname, "src/index.ts"),
        fileName: (format, entryName) => `${entryName}.${format}.js`,
        formats: ["es"],
      },
      sourcemap: true,
      rollupOptions: {
        external: (id: string) => {
          if (toBundle.some((s) => id === s || id.startsWith(`${s}/`))) {
            return false;
          }
          if (id.startsWith(".") || id.startsWith("/")) {
            return false;
          }
          const srcPath = resolve(__dirname, "src");
          if (id.startsWith(srcPath)) {
            return false;
          }
          return true;
        },
      },
    },
    plugins: [flattenSourceMaps(WEBJAR_NODE_OUT_DIR)],
  });
}

function pathsComputation(path: string) {
  const dir = dirname(fileURLToPath(path));
  const packageDirName = basename(dir);
  const pkg = JSON.parse(
    readFileSync(resolve(dir, "package.json"), { encoding: "utf-8" }),
  ) as {
    dependencies?: Record<string, string>;
    peerDependencies?: Record<string, string>;
  };
  return { packageDirName, pkg };
}

function generateConfig(
  path: string,
  distPath: string = "dist",
  entryRoot: string = "./src/",
): UserConfig {
  const { packageDirName, pkg } = pathsComputation(path);
  const externalDependencies = [
    ...Object.keys(pkg.dependencies ?? {}),
    ...Object.keys(pkg.peerDependencies ?? {}),
  ];
  const isCssRequest = (value: string | undefined): boolean =>
    value !== undefined && /\.css($|\?)/.test(value);

  const libFileName = (format: string) => `index.${format}.js`;

  return defineConfig({
    build: {
      sourcemap: true,
      lib: {
        entry: `${entryRoot}/index.ts`,
        name: `cristal_${packageDirName}`,
        fileName: libFileName,
      },
      rollupOptions: {
        external: (id: string) => {
          if (isCssRequest(id)) {
            return false;
          }
          return externalDependencies.some(
            (dependency) =>
              id === dependency || id.startsWith(`${dependency}/`),
          );
        },
      },
    },
    plugins: [
      dts({
        insertTypesEntry: true,
        entryRoot,
        afterBuild: () => {
          const originTypeFile = `${distPath}/index.d.ts`;
          if (existsSync(originTypeFile)) {
            copyFileSync(originTypeFile, `${distPath}/index.d.cts`);
          }
        },
      }),
    ],
  });
}

function generateConfigVue(path: string): UserConfig {
  const baseConfig = generateConfig(path);
  return mergeConfig(
    baseConfig,
    defineConfig({
      build: {
        cssCodeSplit: true,
        rollupOptions: {
          output: {
            globals: {
              vue: "Vue",
            },
          },
        },
      },
      plugins: [...(baseConfig.plugins ?? []), vue(), cssInjectedByJsPlugin()],
    }),
  );
}

export { generateConfig, generateConfigVue, generateWebjarNodeConfig };
