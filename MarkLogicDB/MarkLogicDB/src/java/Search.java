/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.MatchSnippet;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(urlPatterns = {"/Search"})
public class Search extends HttpServlet {
    static final private String OPTIONS_NAME = "line-search";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String searchString = request.getParameter("search");
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Search</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1> You have searched for: <b>" + searchString + "</b> </h1> <br />");
            search(response, out, searchString);
            out.println("</body>");
            out.println("</html>");
        }
    }

    public static void search(HttpServletResponse response, PrintWriter out, String searchString) {
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, "admin", "admin", DatabaseClientFactory.Authentication.DIGEST);

        // create a manager for writing query options
        QueryOptionsManager optionsMgr = client.newServerConfigManager().newQueryOptionsManager();

        // construct the query options
        String options
                = "<search:options "
                + "xmlns:search='http://marklogic.com/appservices/search'>"
                + "<search:constraint name='line'>"
                + "<search:value>"
                + "<search:element name='line' ns=''/>"
                + "</search:value>"
                + "</search:constraint>"
                + "</search:options>";

        // create a handle to send the query options
        StringHandle writeHandle = new StringHandle(options);

        // write the query options to the database
        optionsMgr.writeOptions(OPTIONS_NAME, writeHandle);
        // create a manager for searching
        QueryManager queryMgr = client.newQueryManager();

        // create a query builder for the query options
        StructuredQueryBuilder qb = new StructuredQueryBuilder(OPTIONS_NAME);

        // build a search definition
        StructuredQueryDefinition querydef = qb.and(qb.term(searchString), qb.collectionConstraint("line", searchString));

        // create a handle for the search results
        SearchHandle resultsHandle = new SearchHandle();

        // run the search
        queryMgr.search(querydef, resultsHandle);

        out.println("<h3> It is available in  " + resultsHandle.getTotalResults()+ " documents </h3> <br />");

        // iterate over the result documents
        MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
        for (MatchDocumentSummary docSummary : docSummaries) {
            String uri = docSummary.getUri();
            int score = docSummary.getScore();

            // iterate over the match locations within a result document
            MatchLocation[] locations = docSummary.getMatchLocations();
            out.println("<p> File Location: <b>" + uri +  " </b> </p>");
            for (MatchLocation location : locations) {

                // iterate over the snippets at a match location
                for (MatchSnippet snippet : location.getSnippets()) {
                    boolean isHighlighted = snippet.isHighlighted();

                    if (isHighlighted) {
                        out.print("<i><b>");
                    }
                    out.print(snippet.getText());
                    if (isHighlighted) {
                        out.print("</b></i>");
                    }
                }
                out.println("<br /> <br />");
            }
            out.println("<br />");
            out.println("<a href=''> </a>");
        }
                out.println("<a href='Search.html'> Back </a>");
                

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
