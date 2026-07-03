package ca.ets.log660.labo2.web;

import ca.ets.log660.labo2.facade.CriteresRechercheFilm;
import ca.ets.log660.labo2.facade.WebflixFacade;
import ca.ets.log660.labo2.model.Film;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

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

        if (clientNom == null) {
            response.getWriter().write(Html.page("Connexion requise",
                    "<h1>Connexion requise</h1><p>Vous devez vous connecter avant de rechercher un film.</p>"));
            return;
        }

        body.append("<p>Client connecte: ").append(Html.esc(clientNom)).append("</p>");
        body.append(formulaireRecherche(request));

        try {
            CriteresRechercheFilm criteres = lireCriteres(request);
            if (requeteRecherche(request)) {
                List<Film> films = facade.rechercherFilms(criteres);
                body.append("<h2>Resultats</h2>");
                if(films.size() == 0){
                    body.append("<h3>Aucun film correspondant aux critères insérés</h3>");
                }
                else{
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
                
            }
        } catch (IllegalArgumentException ex) {
            body.append("<p style=\"color:red\">").append(Html.esc(ex.getMessage())).append("</p>");
        }

        response.getWriter().write(Html.page("Recherche de films", body.toString()));
    }

    private String formulaireRecherche(HttpServletRequest request) {
        StringBuilder f = new StringBuilder();
        f.append("<form method=\"get\" action=\"films\"><table cellpadding=\"4\">");
        f.append(ligne("Titre",
                "<input name=\"titre\" value=\"" + Html.esc(request.getParameter("titre")) + "\">"));
        f.append(ligne("Annee",
                "min <input name=\"anneeMin\" size=\"6\" value=\"" + Html.esc(request.getParameter("anneeMin")) + "\"> " +
                "max <input name=\"anneeMax\" size=\"6\" value=\"" + Html.esc(request.getParameter("anneeMax")) + "\">"));
        f.append(ligne("Pays de production",
                casesACocher("pays", facade.listerPays(), valeursSelectionnees(request, "pays"))));
        f.append(ligne("Langue",
                casesACocher("langue", facade.listerLangues(), valeursSelectionnees(request, "langue"))));
        f.append(ligne("Genre",
                casesACocher("genre", facade.listerGenres(), valeursSelectionnees(request, "genre"))));
        f.append(ligne("Realisateur", rechercheRealisateurs(request)));
        f.append(ligne("Acteur", rechercheActeurs(request)));
        f.append("</table>");
        f.append("<p><button type=\"submit\" name=\"chercher\" value=\"1\">Rechercher</button></p>");
        f.append("</form>");
        return f.toString();
    }

    private String rechercheActeurs(HttpServletRequest request){
        Map<Integer, String> acteurs = facade.listerActeurs();
        StringBuilder acteursFiltrer = new StringBuilder();
        acteursFiltrer.append("<input id=\"acteurFilter\" placeholder=\"Nom d'acteur...\" value=\"")
          .append(Html.esc(request.getParameter("acteurQuery")))
          .append("\" oninput=\"filterActeurs()\"> ");
        acteursFiltrer.append("<div id=\"acteurContainer\">");
        acteursFiltrer.append(casesACocherPersonnes("acteur", acteurs, valeursSelectionnees(request, "acteur")));
        acteursFiltrer.append("</div>");

        // Script qui va filtrer les acteurs selon ce qui est rentrer dans le input field
        acteursFiltrer.append("<script>");
        acteursFiltrer.append("function filterActeurs(){var q=document.getElementById('acteurFilter').value.toLowerCase();"
        + "var c=document.getElementById('acteurContainer');if(!c)return;var labels=c.getElementsByTagName('label');"
        + "for(var i=0;i<labels.length;i++){var t=(labels[i].textContent||labels[i].innerText||'').toLowerCase();"
        + "labels[i].style.display=(t.indexOf(q)!==-1)?'block':'none';}}\n");
        acteursFiltrer.append("function clearActeurFilter(){document.getElementById('acteurFilter').value='';filterActeurs();}\n");
        acteursFiltrer.append("document.addEventListener('DOMContentLoaded',filterActeurs);</script>");

        return acteursFiltrer.toString();
    }

    private String rechercheRealisateurs(HttpServletRequest request) {
        Map<Integer, String> realisateurs = facade.listerRealisateurs();
        StringBuilder realisateursFiltrer = new StringBuilder();
        realisateursFiltrer.append("<input id=\"realisateurFilter\" placeholder=\"Nom de réalisateur...\" oninput=\"filterRealisateurs()\"> ");
        realisateursFiltrer.append("<div id=\"realisateurContainer\">");
        realisateursFiltrer.append(selectPersonne("realisateur", realisateurs, request.getParameter("realisateur")));
        realisateursFiltrer.append("</div>");
        
        // Script qui va filtrer les realisateurs selon ce qui est rentrer dans le input field
        realisateursFiltrer.append("<script>");
        realisateursFiltrer.append("function filterRealisateurs(){var q=document.getElementById('realisateurFilter').value.toLowerCase();"
        + "var s=document.querySelector('#realisateurContainer select');if(!s)return;var options=s.options;"
        + "for(var i=0;i<options.length;i++){var opt=options[i];var text=(opt.text||'').toLowerCase();"
        + "var visible=text.indexOf(q)!==-1||opt.selected;opt.hidden=!visible;opt.style.display=visible?'block':'none';}}\n");
        realisateursFiltrer.append("function clearRealisateurFilter(){document.getElementById('realisateurFilter').value='';filterRealisateurs();}\n");
        realisateursFiltrer.append("document.addEventListener('DOMContentLoaded',filterRealisateurs);</script>");
        return realisateursFiltrer.toString();
    }

    private String ligne(String label, String champ) {
        return "<tr><td style=\"vertical-align:top;text-align:right;white-space:nowrap;\"><strong>"
                + Html.esc(label) + ":</strong></td><td>" + champ + "</td></tr>";
    }

    private String casesACocher(String name, List<String> options, Set<String> selected) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"max-height:120px;overflow:auto;border:1px solid #ccc;padding:4px;\">");
        for (String option : options) {
            sb.append("<label style=\"display:block;white-space:nowrap;\">")
                    .append("<input type=\"checkbox\" name=\"").append(name).append("\" value=\"").append(Html.esc(option)).append("\"")
                    .append(selected.contains(option) ? " checked" : "").append("> ")
                    .append(Html.esc(option)).append("</label>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private String selectPersonne(String name, Map<Integer, String> personnes, String selectedId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<select name=\"").append(name).append("\">");
        sb.append("<option value=\"\">-- Tous --</option>");
        for (Map.Entry<Integer, String> entree : personnes.entrySet()) {
            String valeur = String.valueOf(entree.getKey());
            sb.append("<option value=\"").append(valeur).append("\"")
                    .append(valeur.equals(selectedId) ? " selected" : "").append(">")
                    .append(Html.esc(entree.getValue())).append("</option>");
        }
        sb.append("</select>");
        return sb.toString();
    }

    private String casesACocherPersonnes(String name, Map<Integer, String> personnes, Set<String> selectedIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"max-height:120px;overflow:auto;border:1px solid #ccc;padding:4px;\">");
        for (Map.Entry<Integer, String> entree : personnes.entrySet()) {
            String valeur = String.valueOf(entree.getKey());
            sb.append("<label style=\"display:block;white-space:nowrap;\">")
                    .append("<input type=\"checkbox\" name=\"").append(name).append("\" value=\"").append(valeur).append("\"")
                    .append(selectedIds.contains(valeur) ? " checked" : "").append("> ")
                    .append(Html.esc(entree.getValue())).append("</label>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    private Set<String> valeursSelectionnees(HttpServletRequest request, String name) {
        String[] valeurs = request.getParameterValues(name);
        return valeurs == null ? Collections.emptySet() : new HashSet<>(Arrays.asList(valeurs));
    }

    private Integer parseIdOuNull(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(valeur.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private List<String> lireListe(HttpServletRequest request, String name) {
        String[] valeurs = request.getParameterValues(name);
        return valeurs == null ? Collections.emptyList() : Arrays.asList(valeurs);
    }

    private List<Integer> lireListeIds(HttpServletRequest request, String name) {
        String[] valeurs = request.getParameterValues(name);
        if (valeurs == null) {
            return Collections.emptyList();
        }
        List<Integer> ids = new ArrayList<>();
        for (String valeur : valeurs) {
            Integer id = parseIdOuNull(valeur);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    private CriteresRechercheFilm lireCriteres(HttpServletRequest request) {
        CriteresRechercheFilm criteres = new CriteresRechercheFilm();
        criteres.setTitre(request.getParameter("titre"));
        criteres.setPays(lireListe(request, "pays"));
        criteres.setLangues(lireListe(request, "langue"));
        criteres.setGenres(lireListe(request, "genre"));
        criteres.setRealisateurId(parseIdOuNull(request.getParameter("realisateur")));
        criteres.setActeurIds(lireListeIds(request, "acteur"));
        criteres.setAnneeMin(parseAnnee(request.getParameter("anneeMin"), "annee min"));
        criteres.setAnneeMax(parseAnnee(request.getParameter("anneeMax"), "annee max"));
        if (criteres.getAnneeMin() != null && criteres.getAnneeMax() != null
                && criteres.getAnneeMax() < criteres.getAnneeMin()) {
            throw new IllegalArgumentException("L'annee max ne peut pas etre inferieure a l'annee min.");
        }
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
