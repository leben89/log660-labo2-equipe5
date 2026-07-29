package ca.ets.log660.labo2.web;

import ca.ets.log660.labo2.facade.WebflixFacade;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Interface du laboratoire 4 permettant d'analyser le nombre de locations
 * selon le groupe d'age, la province, le jour de la semaine et le mois.
 */
@WebServlet("/analytique")
public class AnalytiqueServlet extends HttpServlet {
    private final WebflixFacade facade = new WebflixFacade();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String groupeAge = filtre(request, "groupeAge");
        String province = filtre(request, "province");
        String jourSemaine = filtre(request, "jourSemaine");
        String moisAnnee = filtre(request, "moisAnnee");

        StringBuilder body = new StringBuilder();
        body.append("<h1>Analytique des locations</h1>")
            .append("<p>Choisissez une valeur ou <strong>Tous</strong> pour chaque dimension.</p>");

        try {
            List<String> groupesAge = facade.listerGroupesAgeAnalytiques();
            List<String> provinces = facade.listerProvincesAnalytiques();
            List<String> jours = facade.listerJoursAnalytiques();
            List<String> mois = facade.listerMoisAnalytiques();

            long nombreLocations = facade.compterLocationsAnalytiques(
                    groupeAge, province, jourSemaine, moisAnnee);

            body.append("<form method=\"get\" action=\"analytique\">")
                .append("<table>")
                .append(ligneSelect("Groupe d'âge", "groupeAge", groupesAge, groupeAge))
                .append(ligneSelect("Province", "province", provinces, province))
                .append(ligneSelect("Jour de la semaine", "jourSemaine", jours, jourSemaine))
                .append(ligneSelect("Mois", "moisAnnee", mois, moisAnnee))
                .append("</table>")
                .append("<p><button type=\"submit\">Calculer</button></p>")
                .append("</form>")
                .append("<h2>Résultat</h2>")
                .append("<p>Nombre de locations : <strong>")
                .append(nombreLocations)
                .append("</strong></p>")
                .append("<p>Filtres appliqués : ")
                .append(Html.esc(groupeAge)).append(" / ")
                .append(Html.esc(province)).append(" / ")
                .append(Html.esc(jourSemaine)).append(" / ")
                .append(Html.esc(moisAnnee)).append("</p>");
        } catch (RuntimeException ex) {
            body.append("<p><strong>Impossible de lire l'entrepôt de données.</strong></p>")
                .append("<p>Exécutez d'abord les scripts ")
                .append("<code>01_schema_etoile.sql</code> puis ")
                .append("<code>02_etl_locations_synthetiques.sql</code>.</p>")
                .append("<pre>").append(Html.esc(ex.getMessage())).append("</pre>");
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(Html.page("Analytique des locations", body.toString()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String filtre(HttpServletRequest request, String nom) {
        String valeur = request.getParameter(nom);
        return valeur == null || valeur.trim().isEmpty()
                ? "Tous"
                : valeur.trim();
    }

    private String ligneSelect(String libelle,
                               String nom,
                               List<String> valeurs,
                               String selection) {
        StringBuilder html = new StringBuilder();
        html.append("<tr><td><label for=\"")
            .append(Html.esc(nom)).append("\">")
            .append(Html.esc(libelle)).append("</label></td><td>")
            .append("<select id=\"").append(Html.esc(nom))
            .append("\" name=\"").append(Html.esc(nom)).append("\">");

        option(html, "Tous", selection);
        for (String valeur : valeurs) {
            option(html, valeur, selection);
        }

        html.append("</select></td></tr>");
        return html.toString();
    }

    private void option(StringBuilder html, String valeur, String selection) {
        html.append("<option value=\"").append(Html.esc(valeur)).append("\"");
        if (valeur.equals(selection)) {
            html.append(" selected");
        }
        html.append(">").append(Html.esc(valeur)).append("</option>");
    }
}
