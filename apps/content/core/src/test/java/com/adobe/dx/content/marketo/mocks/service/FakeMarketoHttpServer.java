/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright 2020 Adobe
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobe.dx.content.marketo.mocks.service;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.apache.http.entity.mime.MIME.UTF8_CHARSET;

import com.adobe.dx.content.mocks.FakeHttpServer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.servlet.ServletHolder;

public class FakeMarketoHttpServer extends FakeHttpServer {

    private static final String MIME_APPLICATION_JSON = "application/json";
    private static final String MIME_TEXT_XML = "text/xml";

    public FakeMarketoHttpServer(int bindPort, String servletContext) {
        super(bindPort, servletContext);
        initMockServlets();
    }

    private void initMockServlets() {
        this.addServlet(new ServletHolder(new FakeMarketoAuthServlet()), "/identity/oauth/token");
        this.addServlet(new ServletHolder(new FakeMarketoFormsServlet()), "/rest/asset/v1/forms.json");
    }

    public class FakeMarketoAuthServlet extends HttpServlet {

        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String clientSecret = req.getParameter("client_secret");
            if (StringUtils.equals(clientSecret, "validClientSecret")) {
                writeResponse(resp, SC_OK, MIME_APPLICATION_JSON,
                    "/mocks/marketo/client/authToken/success.json");
            } else {
                writeResponse(resp, SC_UNAUTHORIZED, MIME_TEXT_XML,
                    "/mocks/marketo/client/authToken/failure.xml");
            }
        }

    }

    public class FakeMarketoFormsServlet extends HttpServlet {

        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String accessToken = req.getParameter("access_token");
            writeResponse(resp, SC_OK, MIME_APPLICATION_JSON,
                StringUtils.equals(accessToken, "validAuthToken")
                    ? "/mocks/marketo/client/formData/success.json"
                    : "/mocks/marketo/client/formData/failure.json");
        }

    }

    private void writeResponse(HttpServletResponse resp, int statusCode, String contentType,
                               String classPathResponseFile) throws IOException {
        String content = IOUtils.toString(getClass().getResourceAsStream(classPathResponseFile), UTF8_CHARSET);
        resp.setStatus(statusCode);
        resp.setContentType(contentType);
        resp.getWriter().write(content);
    }

}
