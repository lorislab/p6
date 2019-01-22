/*
 * Copyright 2019 lorislab.org.
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

package org.lorislab.p6.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.*;

@WebServlet(urlPatterns = {"/openapi.json", "/openapi.yaml"})
public class OpenApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getServletPath();
        response.setContentType(MediaType.APPLICATION_JSON);
        PrintWriter writer = response.getWriter();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fileName)))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                writer.println(line);
            }
        }
    }
}
