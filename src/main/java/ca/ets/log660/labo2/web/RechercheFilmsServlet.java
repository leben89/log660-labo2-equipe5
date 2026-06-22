package ca.ets.log660.labo2.web;

import ca.ets.log660.labo2.facade.CriteresRechercheFilm;
import ca.ets.log660.labo2.facade.WebflixFacade;
import ca.ets.log660.labo2.model.Film;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/films")
public class RechercheFilmsServlet extends HttpServlet {
    private final WebflixFacade facade = new WebflixFacade();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        StringBuilder body = new StringBuilder();
        Object clientNom = request.getSession(false) == null ? null : request.getSession(false).getAttribute("clientNom");
        body.append("<h1>Recherche de films</h1>");
        if (clientNom != null) {
            body.append("<p>Client connecte: ").append(Html.esc(clientNom)).append("</p>");
        }
        body.append(formulaireRecherche(request));

        try {
            CriteresRechercheFilm criteres = lireCriteres(request);
            if (requeteRecherche(request)) {
                List<Film> films = facade.rechercherFilms(criteres);
                body.append("<h2>Resultats</h2>");
                body.append("<p>").append(films.size()).append(" film(s) trouve(s).</p>");
                body.append("<table border=\"1\" cellpadding=\"5\"><tr><th>Titre</th><th>Annee</th><th>Langue</th><th>Action</th></tr>");
                for (Film film : films) {
                    body.append("<tr>")
                            .append("<td>").append(Html.esc(film.getTitre())).append("</td>")
                            .append("<td>").append(Html.esc(film.getAnnee())).append("</td>")
                            .append("<td>").append(Html.esc(film.getLangue())).append("</td>")
                            .append("<td><a href=\"film?id=").append(film.getId()).append("\">Consulter</a></td>")
                            .append("</tr>");
                }
                body.append("</table>");
            }
        } catch (IllegalArgumentException ex) {
            body.append("<p style=\"color:red\">").append(Html.esc(ex.getMessage())).append("</p>");
        }

        response.getWriter().write(Html.page("Recherche de films", body.toString()));
    }

    private String formulaireRecherche(HttpServletRequest request) {
        return "<form method=\"get\" action=\"films\">" +
                "<p>Titre: <input name=\"titre\" value=\"" + Html.esc(request.getParameter("titre")) + "\"></p>" +
                "<p>Annee min: <input name=\"anneeMin\" value=\"" + Html.esc(request.getParameter("anneeMin")) + "\"> " +
                "Annee max: <input name=\"anneeMax\" value=\"" + Html.esc(request.getParameter("anneeMax")) + "\"></p>" +
                "<p>Pays de production: <input name=\"pays\" value=\"" + Html.esc(request.getParameter("pays")) + "\"></p>" +
                "<p>Langue: <input name=\"langue\" value=\"" + Html.esc(request.getParameter("langue")) + "\"></p>" +
                "<p>Genre: <input name=\"genre\" value=\"" + Html.esc(request.getParameter("genre")) + "\"></p>" +
                "<p>Realisateur: <input name=\"realisateur\" value=\"" + Html.esc(request.getParameter("realisateur")) + "\"></p>" +
                "<p>Acteur: <input name=\"acteur\" value=\"" + Html.esc(request.getParameter("acteur")) + "\"></p>" +
                "<p><button type=\"submit\" name=\"chercher\" value=\"1\">Rechercher</button></p>" +
                "</form>";
    }

    private CriteresRechercheFilm lireCriteres(HttpServletRequest request) {
        CriteresRechercheFilm criteres = new CriteresRechercheFilm();
        criteres.setTitre(request.getParameter("titre"));
        criteres.setPays(request.getParameter("pays"));
        criteres.setLangue(request.getParameter("langue"));
        criteres.setGenre(request.getParameter("genre"));
        criteres.setRealisateur(request.getParameter("realisateur"));
        criteres.setActeur(request.getParameter("acteur"));
        criteres.setAnneeMin(parseAnnee(request.getParameter("anneeMin"), "annee min"));
        criteres.setAnneeMax(parseAnnee(request.getParameter("anneeMax"), "annee max"));
        return criteres;
    }

    private Integer parseAnnee(String valeur, String libelle) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return null;
        }
        try {
            int annee = Integer.parseInt(valeur.trim());
            if (annee < 1888) {
                throw new IllegalArgumentException("Le format de " + libelle + " est invalide.");
            }
            return annee;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Le format de " + libelle + " est invalide.");
        }
    }

    private boolean requeteRecherche(HttpServletRequest request) {
        return request.getParameter("chercher") != null;
    }
}
