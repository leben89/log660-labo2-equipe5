package ca.ets.log660.labo2.web;

import ca.ets.log660.labo2.facade.WebflixFacade;
import ca.ets.log660.labo2.model.Client;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/connexion")
public class ConnexionServlet extends HttpServlet {
    private final WebflixFacade facade = new WebflixFacade();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        afficherFormulaire(response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String courriel = request.getParameter("courriel");
        String motDePasse = request.getParameter("motDePasse");

        if (courriel == null || courriel.trim().isEmpty() || motDePasse == null || motDePasse.trim().isEmpty()) {
            afficherFormulaire(response, "Le courriel et le mot de passe sont obligatoires.");
            return;
        }

        Client client = facade.connecter(courriel, motDePasse);
        if (client == null) {
            afficherFormulaire(response, "Connexion refusee: courriel ou mot de passe invalide.");
            return;
        }

        request.getSession(true).setAttribute("clientId", client.getId());
        request.getSession().setAttribute("clientNom", client.getNomComplet());
        response.sendRedirect(request.getContextPath() + "/films");
    }

    private void afficherFormulaire(HttpServletResponse response, String erreur) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        String message = erreur == null ? "" : "<p style=\"color:red\">" + Html.esc(erreur) + "</p>";
        String body = "<h1>Connexion d'un client</h1>" + message +
                "<form method=\"post\" action=\"connexion\">" +
                "<p>Courriel: <input type=\"email\" name=\"courriel\" required></p>" +
                "<p>Mot de passe: <input type=\"password\" name=\"motDePasse\" required></p>" +
                "<p><button type=\"submit\">Se connecter</button></p>" +
                "</form>";
        response.getWriter().write(Html.page("Connexion", body));
    }
}
