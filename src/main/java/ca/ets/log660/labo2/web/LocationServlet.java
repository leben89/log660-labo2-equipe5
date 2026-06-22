package ca.ets.log660.labo2.web;

import ca.ets.log660.labo2.facade.WebflixFacade;
import ca.ets.log660.labo2.model.Location;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/louer")
public class LocationServlet extends HttpServlet {
    private final WebflixFacade facade = new WebflixFacade();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Integer clientId = session == null ? null : (Integer) session.getAttribute("clientId");
        if (clientId == null) {
            response.getWriter().write(Html.page("Connexion requise",
                    "<h1>Connexion requise</h1><p>Vous devez vous connecter avant de louer un film.</p>"));
            return;
        }

        Integer filmProduitId = parseId(request.getParameter("filmProduitId"));
        if (filmProduitId == null) {
            response.getWriter().write(Html.page("Location impossible",
                    "<h1>Location impossible</h1><p>Identifiant du produit invalide.</p>"));
            return;
        }

        try {
            Location location = facade.louerFilm(clientId, filmProduitId);
            response.getWriter().write(Html.page("Location confirmee",
                    "<h1>Location confirmee</h1><p>Numero de location: " + Html.esc(location.getId()) + "</p>"));
        } catch (RuntimeException ex) {
            response.getWriter().write(Html.page("Location refusee",
                    "<h1>Location refusee</h1><p style=\"color:red\">" + Html.esc(ex.getMessage()) + "</p>"));
        }
    }

    private Integer parseId(String id) {
        try {
            return id == null ? null : Integer.valueOf(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
