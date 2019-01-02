package si.photos.by.d.robot.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import si.photos.by.d.robot.models.entities.RobotPicture;
import si.photos.by.d.robot.services.configuration.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RequestScoped
public class RobotBean {
    private Logger log = Logger.getLogger(RobotBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    private Client httpClient;

    @Inject
    @DiscoverService("photo-management-service")
    private Optional<String> photoUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    public List<RobotPicture> getExistingPictures() {
        TypedQuery<RobotPicture> query = em.createQuery("SELECT r FROM robot_picture r", RobotPicture.class);
        return query.getResultList();
    }

    public RobotPicture getOrCreteRobotPicture(String hash) {
        TypedQuery<RobotPicture> query = em.createQuery("SELECT r FROM robot_picture r WHERE r.hash = :hash", RobotPicture.class);
        query.setParameter("hash", hash);
        RobotPicture robotPicture = null;
        try {
           robotPicture = query.getSingleResult();
        }
        catch (NoResultException noResult){
            try {
                HttpResponse<JsonNode> response = Unirest.get("https://robohash.p.rapidapi.com/index.php?text=masHape")
                        .header("X-RapidAPI-Key", appProperties.getRapidApi())
                        .queryString("text", hash)
                        .asJson();
                robotPicture = new RobotPicture();
                robotPicture.setHash(hash);
                robotPicture.setImageUrl(response.getBody().getObject().getString("imageUrl"));
                try {
                    beginTx();
                    em.persist(robotPicture);
                    commitTx();
                } catch (Exception e) {
                    log.warning("There was a problem with saving new robot picture with hash " + robotPicture.getHash());
                    rollbackTx();
                }
                log.info("Successfully saved new robot picture with hash " + robotPicture.getHash());
            } catch (UnirestException e) {
                log.warning("There was a problem getting response from robot hash api ");
                e.printStackTrace();
            }
        }
        return robotPicture;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
