package pl.edu.pg.pcej.Map;

import javafx.beans.Observable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pg.pcej.ClusterMapController;

/**
 *
 * @author johan
 */
public class MapTile extends Region {

    private static final Logger log = LoggerFactory.getLogger(ClusterMapController.class);
    public static final String OPENSTREETMAP_URL = "http://tile.openstreetmap.org/";
    public static final int TILE_SIDE_SIZE = 256;
    private final int tileZoom;
    private final long i, j;

    private Image image;
    private Scale scale;

    public MapTile(int tileZoom, long i, long j) {
        this.tileZoom = tileZoom;
        this.i = i;
        this.j = j;
        Label debugLabel = new Label();
        //debugLabel.setText("[" + i + "," + j + "]@" + tileZoom);
        log.trace("create " + this);
        String url = OPENSTREETMAP_URL + tileZoom + "/" + i + "/" + j + ".png";
        log.debug("Openstreetmap request: " + url);
        image = new Image(url, true);
        ImageView imageView = new ImageView(image);
        scale = new Scale();
        scale.setPivotX(0);
        scale.setPivotY(0);
        getTransforms().add(scale);
        getChildren().addAll(imageView, debugLabel);
        ClusterMapController.scale.addListener(o -> calculatePosition());
        calculatePosition();
    }

    private void calculatePosition() {
        double scaleFactor = ClusterMapController.scale.get();
        double sf = Math.pow(2, scaleFactor - tileZoom);
        scale.setX(sf);
        scale.setY(sf);
        setTranslateX(TILE_SIDE_SIZE * i * sf);
        setTranslateY(TILE_SIDE_SIZE  * j * sf);
    }

    @Override
    public String toString() {
        return "Tile["+i+","+j+"] at zoomlevel "+tileZoom;
    }

}
