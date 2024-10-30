package entity;

import main.GamePanel;
import main.KeyHandler;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import main.Helpers;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage; 

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    public boolean speedBoostActive = false;
    public long speedBoostStartTime;
    public boolean blinkMessage = false;

    public int keyCount = 0;


    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle(6, 14, 28, 28);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "right";
    }

    public void getPlayerImage() {
        up1 = setup("boy_up_1");
        up2 = setup("boy_up_2");
        down1 = setup("boy_down_1");
        down2 = setup("boy_down_2");
        left1 = setup("boy_left_1");
        left2 = setup("boy_left_2");
        right1 = setup("boy_right_1");
        right2 = setup("boy_right_2");
    }

    public BufferedImage setup(String name) {
        Helpers scaler = new Helpers();
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/player/" + name + ".png"));
            image = scaler.scaleImage(image, gp.tileSize, gp.tileSize);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void interactObject(int i) {
        //if 999, we haven't touched an object
        if (i != 999) {
            String objectName = gp.obj[i].name;

            switch (objectName) {
                case "key":
                    gp.playSoundEffect(1);
                    keyCount++;
                    gp.obj[i] = null;
                    break;
                case "door":
                    if (keyCount > 0) {
                        gp.playSoundEffect(3);
                        gp.obj[i] = null;
                        keyCount--;
                    }
                    break;
                case "boots":
                    gp.playSoundEffect(2);
                    activateSpeedBoost();
                    gp.obj[i] = null;
                    break;
            }
        }
    }

    public int getHasKey() {
        return keyCount;
    }

    public void activateSpeedBoost() {
        if (speedBoostActive) {
            speedBoostStartTime += 30000; 
        } else {
            speed += 2;
            speedBoostActive = true;
            speedBoostStartTime = System.currentTimeMillis();
        }
    }

    public void updateSpeedBoost() {
        if (speedBoostActive) {
            long elapsedTime = (System.currentTimeMillis() - speedBoostStartTime) / 1000;

            //blink the speed boost message 5 secs before it ends
            if (elapsedTime >= 25 && elapsedTime < 30) {
                blinkMessage = true;
            }
            else if (elapsedTime >= 30) {
                speed -= 2;
                speedBoostActive = false;
                blinkMessage = false;
            }
        }
    }

    public int getRemainingBoostTime() {
        if (speedBoostActive) {
            long elapsedTime = (System.currentTimeMillis() - speedBoostStartTime) / 1000;
            return 30 - (int) elapsedTime; // Calculate remaining time in seconds
        }
        return 0; // No speed boost active
    }

    public void update() {

        //if not moving in a direction, don't increment spritecounter for walking effect
        if (keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true) {

            if (keyH.upPressed == true) {
                direction = "up";
            }
            if (keyH.downPressed == true) {
                direction = "down";
            }
            if (keyH.leftPressed == true) {
                direction = "left";       
            }
            if (keyH.rightPressed == true) {
                direction = "right";
            }
 
            collisionOn = false;
            gp.checker.checkTile(this);

            int objIndex = gp.checker.checkObject(this, true);
            interactObject(objIndex);

            if (collisionOn == false) {
                switch (direction) {
                    case "up": worldY -= speed; break; 
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }  
            spriteCounter ++;
            //change 13 in order to change speed at which sprites switch
            if (spriteCounter > 13) {
                if (spriteNumber == 1) {
                    spriteNumber = 2;
                }
                else if (spriteNumber == 2) {
                    spriteNumber = 1;
                }
                spriteCounter = 0;
            }
        }
        updateSpeedBoost();
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case "up": 
                if (spriteNumber == 1) {
                    image = up1;
                }
                if (spriteNumber == 2) {
                    image = up2;
                }
                break;

            case "down":
                if (spriteNumber ==  1) {
                    image = down1;
                }
                if (spriteNumber == 2) {
                    image = down2;
                }
                break;

            case "left": 
                if (spriteNumber ==  1) {
                    image = left1;
                }
                if (spriteNumber == 2) {
                    image = left2;
                }
                break;

            case "right":
                if (spriteNumber == 1) {
                    image = right1;
                }
                if (spriteNumber == 2) {
                    image = right2;
                }
                break;
        }   
        g2.drawImage(image, screenX, screenY, null);
    }
}   