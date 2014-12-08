package pl.edu.pg.pcej;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pg.pcej.Map.MapTile;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ClusterMapController implements Initializable
{
    private static final Logger log = LoggerFactory.getLogger(ClusterMapController.class);
    private double x0;
    private double y0;
    @FXML private TableView tableView;
    @FXML private Group mapGroup;
    @FXML private Pane pane;
    @FXML private Slider scaleSlider;
    @FXML private ToggleButton toggleButton;

    final static int MAX_ZOOM = 20;
    private final Map<Long, MapTile>[] tiles = new HashMap[MAX_ZOOM];
    public static DoubleProperty scale = new SimpleDoubleProperty(.0);
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleButton.selectedProperty().bindBidirectional(tableView.visibleProperty());
        initVisibilityBindings(toggleButton);
        initVisibilityBindings(scaleSlider);

        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = new HashMap<>();
        }
        final MapTile rootTile = new MapTile(0,0,0);
        tiles[0].put(0L, rootTile);
        mapGroup.getChildren().add(rootTile);
        scale.bindBidirectional(scaleSlider.valueProperty());
        scaleSlider.setMax(MAX_ZOOM - 1);
        pane.setOnMousePressed(t -> {
            x0 = t.getSceneX();
            y0 = t.getSceneY();
        });
        pane.setOnMouseDragged(t -> {
            double dx = x0 - t.getSceneX();
            double dy = y0 - t.getSceneY();
            if(mapGroup.getTranslateX() >= -256*Math.pow(2,scale.doubleValue()) || dx <0) {
                mapGroup.setTranslateX(mapGroup.getTranslateX() - dx);
            }
            if(mapGroup.getTranslateY() >= -256*Math.pow(2,scale.doubleValue()) || dy < 0) {
                mapGroup.setTranslateY(mapGroup.getTranslateY() - dy);
            }
            x0 = t.getSceneX();
            y0 = t.getSceneY();
        });
        pane.setOnMouseReleased(t -> checkTiles()) ;
        pane.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY() > 0 ? .1 : -.1;
            scale.set(scale.get() + deltaY);
        });
        scale.addListener(o -> checkTiles());
    }

    private void checkTiles() {
        double currentScale = scale.get();
        int lowScale = (int) currentScale;
        long max_i = 1 << lowScale;
        long max_j = 1 << lowScale;
        double tx = mapGroup.getTranslateX();
        double ty = mapGroup.getTranslateY();
        double width = mapGroup.getScene().getWidth();
        double height = mapGroup.getScene().getHeight();
        double deltaZ = currentScale - lowScale;

        long imin = Math.max(0, (long) (-tx * Math.pow(2, deltaZ) / 256) - 1);
        long jmin = Math.max(0, (long) (-ty * Math.pow(2, deltaZ) / 256) - 1 );
        long imax = Math.min(max_i, imin + (long) (width * Math.pow(2, deltaZ) / 256) + 1);
        long jmax = Math.min(max_j, jmin + (long) (height * Math.pow(2, deltaZ) / 256) + 1);
        log.debug("dz = "+deltaZ +"imax = "+imax+",imin = "+imin+", tx = "+tx+", max_i= "+max_i);
        log.debug("        jmax = "+jmax+",jmin = "+jmin+", ty = "+ty+", max_j= "+max_j);

        for (long i = imin; i <= imax; i++) {
            for (long j = jmin; j <= jmax; j++) {
                long key = i * max_i + j;
                MapTile exists = tiles[lowScale].get(key);
                log.trace("we need [" + i + "," + j + "], and we have " + exists);
                if (exists == null) {
                    MapTile newTile = new MapTile(lowScale, i, j);
                    tiles[lowScale].put(key, newTile);
                    mapGroup.getChildren().add(newTile);
                }
            }
        }
    }
    private void initVisibilityBindings(Control control){
        control.setOnMouseExited(event -> control.setOpacity(.5));
        control.setOnMouseEntered(event -> control.setOpacity(1));
    }
    private int getMaxTranslateX(){
        int maxTranslation = 0;

        return maxTranslation;
    }
}
