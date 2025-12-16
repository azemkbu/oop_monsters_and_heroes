package game.mh;

import game.Game;
import utils.IOUtils;
import worldMap.enums.Direction;

public interface MHCommandGame extends Game {
  void handleMove(Direction direction);
  void handleEnterMarket();
  void showPartyInfo();
  IOUtils getIo();
}
