/*-
 * =================================LICENSE_START==================================
 * lambdainternals
 * ====================================SECTION=====================================
 * Copyright (C) 2023 Andy Boothe
 * ====================================SECTION=====================================
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
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.lambdainternals;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class App implements RequestHandler<String, String> {
  public static void main(String[] args) {
    new App().handleRequest("ping", null);
  }

  public String handleRequest(String input, Context context) {
    System.out.println("==== MEMORY INFO ====");
    printFileContents(System.out, new File("/proc/meminfo"));
    System.out.println();

    System.out.println("==== CPU INFO ====");
    printFileContents(System.out, new File("/proc/cpuinfo"));
    System.out.println();

    System.out.println("==== VERSION INFO ====");
    printFileContents(System.out, new File("/proc/version"));
    System.out.println();

    System.out.println("==== DISK INFO ====");
    printFileContents(System.out, new File("/proc/diskstats"));
    System.out.println();

    System.out.println("==== COMMAND LINE ====");
    printFileContents(System.out, new File("/proc/self/cmdline"), s -> s.replace('\0', ' '));
    System.out.println();

    System.out.println("==== ENVIRONMENT ====");
    printFileContents(System.out, new File("/proc/self/environ"), s -> s.replace('\0', '\n'));
    System.out.println();

    return input;
  }

  public static void printFileContents(PrintStream out, File f) {
    printFileContents(out, f, Function.identity());
  }

  public static void printFileContents(PrintStream out, File f, Function<String,String> transformation) {
    out.println(transformation.apply(readFileContents(f)));
    out.println();
  }

  public static String readFileContents(File f) {
    StringBuilder result=new StringBuilder();

    try (Reader r=new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
      char[] buf=new char[16384];
      for(int nread=r.read(buf, 0, buf.length);nread!=-1;nread=r.read(buf, 0, buf.length)) {
        result.append(buf, 0, nread);
      }
    }
    catch(IOException e) {
      throw new UncheckedIOException("Failed to read file "+f.getPath(), e);
    }

    return result.toString();
  }
}
