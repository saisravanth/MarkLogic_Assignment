import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;


public class List extends HttpServlet {

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
            throws ServletException, IOException, XPathExpressionException {
        response.setContentType("text/html;charset=UTF-8");
        String orderBy = request.getParameter("order");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet List Titles</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>List all the Titles in " + orderBy + " order </h2>");
            getTitles(response, out, orderBy);
            out.println("</body>");
            out.println("</html>");

        }
    }

    public static void getTitles(HttpServletResponse response, PrintWriter out, String orderBy) throws IOException, XPathExpressionException {
        // create the client
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, "admin", "admin", DatabaseClientFactory.Authentication.DIGEST);

        ArrayList<String> titles = new ArrayList<String>();
        XMLDocumentManager xml = client.newXMLDocumentManager();
        QueryManager qm = client.newQueryManager();
        StringQueryDefinition query = qm.newStringDefinition();

        DocumentPage documents = xml.search(query, 1);
        while (documents.hasNext()) {
            DocumentRecord document = documents.next();
            // do something with the contents
            if (document.getUri().contains(".xml")) {

                String docId = document.getUri();

                // create a handle to receive the document content
                DOMHandle handle1 = new DOMHandle();

                XMLDocumentManager docMgr = client.newXMLDocumentManager();

                // read the document content
                docMgr.read(docId, handle1);

                Document document1 = handle1.get();

                // apply an XPath 1.0 expression to the document
                String title = handle1.evaluateXPath("string(/PLAY/TITLE)", String.class);
                titles.add(title);
            }
        }
        // release the client
        client.release();

        if (orderBy.equals("ascending")) {
            Collections.sort(titles);
            prettyPrint(titles, out);
        } else {
            Collections.sort(titles, Collections.reverseOrder());
            prettyPrint(titles, out);
        }
        out.println("<a href='List.html'> Back </a>");
    }

    public static void prettyPrint(ArrayList titles, PrintWriter out) {
        out.println("<ul>");
        Enumeration<String> e = Collections.enumeration(titles);
        while (e.hasMoreElements()) {
            out.println("<li>" + e.nextElement() + "</li>");
        }
        out.println("</ul>");
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
        try {
            processRequest(request, response);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(List.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(List.class.getName()).log(Level.SEVERE, null, ex);
        }
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
