import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class assign6 extends PApplet {

class GameState
{
	static final int START = 0;
	static final int PLAYING = 1;
	static final int END = 2;
}
class Direction
{
	static final int LEFT = 0;
	static final int RIGHT = 1;
	static final int UP = 2;
	static final int DOWN = 3;
}
class EnemysShowingType
{
	static final int STRAIGHT = 0;
	static final int SLOPE = 1;
	static final int DIAMOND = 2;
	static final int STRONGLINE = 3;
}
class FlightType
{
	static final int FIGHTER = 0;
	static final int ENEMY = 1;
	static final int ENEMYSTRONG = 2;
}

int state = GameState.START;
int currentType = EnemysShowingType.STRAIGHT;
int enemyCount = 8;
Enemy[] enemys = new Enemy[enemyCount];
Fighter fighter;
Background bg;
FlameMgr flameMgr;
Treasure treasure;
HPDisplay hpDisplay;
boolean isMovingUp;
boolean isMovingDown;
boolean isMovingLeft;
boolean isMovingRight;
int bulletCount = 5;
Bullet[] bullets = new Bullet[bulletCount];
int time;
int wait = 4000;



public void setup () {
	
	flameMgr = new FlameMgr();
	bg = new Background();
	treasure = new Treasure();
	hpDisplay = new HPDisplay();
	fighter = new Fighter(20);
}

public void draw()
{	
	
	if (state == GameState.START) {
		bg.draw();	
	}
	else if (state == GameState.PLAYING) {
		bg.draw();
		treasure.draw();
		flameMgr.draw();
		fighter.draw();

		//enemys
		if(millis() - time >= wait){
			addEnemy(currentType++);
			currentType = currentType%4;
		}		

		for (int i = 0; i < enemyCount; ++i) {
			if (enemys[i]!= null) {
				enemys[i].move();
				enemys[i].draw();
				if (enemys[i].isCollideWithFighter()) {
					fighter.hpValueChange(-20);
					flameMgr.addFlame(enemys[i].x, enemys[i].y);
					enemys[i]=null;
				}
				for(int j = 0;j<5;j++){
					if (bullets[j] != null && enemys[i] != null) {
						if (enemys[i].isCollideWithBullet(bullets[j])) {
							flameMgr.addFlame(enemys[i].x, enemys[i].y);
							bullets[j] = null;
							enemys[i] = null;
							}
						}
					}
				}
		}

		// \u9019\u5730\u65b9\u61c9\u8a72\u52a0\u5165Fighter \u8840\u91cf\u986f\u793aUI
		hpDisplay.updateWithFighterHP(fighter.hp);
		
		//bullets
		for(int i = 0;i<bulletCount;i++){
			if(bullets[i] != null){
				bullets[i].move();
				bullets[i].draw();
			}
		}
	

		//bullets crush	
	}else if (state == GameState.END) {
		bg.draw();
	}
}
public boolean isHit(int ax, int ay, int aw, int ah, int bx, int by, int bw, int bh)
{
	// Collision x-axis?
    boolean collisionX = (ax + aw >= bx) && (bx + bw >= ax);
    // Collision y-axis?
    boolean collisionY = (ay + ah >= by) && (by + bh >= ay);
    return collisionX && collisionY;
}

public void keyPressed(){
  switch(keyCode){
    case UP : isMovingUp = true ;break ;
    case DOWN : isMovingDown = true ; break ;
    case LEFT : isMovingLeft = true ; break ;
    case RIGHT : isMovingRight = true ; break ;
    default :break ;
  }
}
public void keyReleased(){
  switch(keyCode){
	case UP : isMovingUp = false ;break ;
    case DOWN : isMovingDown = false ; break ;
    case LEFT : isMovingLeft = false ; break ;
    case RIGHT : isMovingRight = false ; break ;
    default :break ;
  }
  if (key == ' ') {
  	if (state == GameState.PLAYING) {
		fighter.shoot();
	}
  }
  if (key == ENTER) {
    switch(state) {
      case GameState.START:
      case GameState.END:
        state = GameState.PLAYING;
		enemys = new Enemy[enemyCount];
		flameMgr = new FlameMgr();
		treasure = new Treasure();
		fighter = new Fighter(20);
      default : break ;
    }
  }
}

class Background{
	PImage start1;
	PImage start2;

	PImage bg1;
	PImage bg2;
	
	PImage end1;
	PImage end2;


	int playingBg1x = 0;
	int playingBg2x = -640;

	Background() {
		//since this won't change in the future
		this.bg1 = loadImage("img/bg1.png");
		this.bg2 = loadImage("img/bg2.png");

		this.start1 = loadImage("img/start1.png");
		this.start2 = loadImage("img/start2.png");

		this.end1 = loadImage("img/end1.png");
		this.end2 = loadImage("img/end2.png");
	}

	public void draw()
	{
		if (state == GameState.START) {
			if (second() % 2 == 1 ) {
				image(start1, 0, 0);
			}
			else {
				image(start2, 0, 0);
			}
		}
		else if (state == GameState.PLAYING) {
			playingBg1x++;
			playingBg2x++;

			if (playingBg1x == 640) {
				playingBg1x = -640;
			}

			if (playingBg2x == 640) {
				playingBg2x = -640;
			}

			image(bg1, playingBg1x, 0);
			image(bg2, playingBg2x, 0);
		}
		else if (state == GameState.END) {
			if (second() % 2 == 1 ) {
				image(end1, 0, 0);
			}
			else {
				image(end2, 0, 0);
			}
		}
	}

}
// Boss image is "img/enemy2.png" 
class Boss{

}
class Bullet{
	PImage bullet;
	int x = 0;
	int y = 0;
	Bullet(int x, int y) {
		this.x = x;
		this.y = y;
		this.bullet = loadImage("img/shoot.png");
	}

	public void draw()
	{
		image(bullet, x, y);
	}
	public void move(){
		this.x -= 3;	
	}

	public boolean isCollideWithEnemy(Enemy enemys)
	{
		if (isHit(this.x, this.y, this.bullet.width, this.bullet.height, enemys.x, enemys.y, enemys.enemyImg.width, enemys.enemyImg.height)) {
		return true;
		}
		return false;
	}
}
class Enemy{
	int x = 0;
	int y = 0;
	int type;
	int speed = 5;

	PImage enemyImg;
	Enemy(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
		enemyImg = loadImage("img/enemy.png");
		
	}
	public void move() {
		this.x+= 5;	
	}

	public void draw()
	{
		image(enemyImg, x, y);
	}

	public boolean isCollideWithFighter()
	{
		if (isHit(this.x, this.y, this.enemyImg.width, this.enemyImg.height, fighter.x, fighter.y, fighter.fighterImg.width, fighter.fighterImg.height)) {
		return true;
		}
		return false;
	}

	public boolean isCollideWithBullet(Bullet bullet)
	{
		if (isHit(this.x, this.y, this.enemyImg.width, this.enemyImg.height, bullet.x, bullet.y, bullet.bullet.width, bullet.bullet.height)) {
		return true;
		}
		return false;
	}

	public boolean isOutOfBorder()
	{
		if(x > 640 || y > 480){
			return true;
		}else {
			return false;
		}
	}


}

public void addEnemy(int type)
{	
	for (int i = 0; i < enemyCount; ++i) {
		enemys[i] = null;
	}
	switch (type) {
		case EnemysShowingType.STRAIGHT:
			addStraightEnemy();
			break;
		case EnemysShowingType.SLOPE:
			addSlopeEnemy();
			break;
		case EnemysShowingType.DIAMOND:
			addDiamondEnemy();
			break;
		case EnemysShowingType.STRONGLINE:
			addEnemyStrong();
			break;
	}
	time = millis();
}

public void addStraightEnemy()
{
	float t = random(height - 60);
	int h = PApplet.parseInt(t);
	for (int i = 0; i < 5; ++i) {
		enemys[i] = new Enemy( (i+1)*-80, h , FlightType.ENEMY);
	}
}
public void addSlopeEnemy()
{
	float t = random(height - 60 * 5);
	int h = PApplet.parseInt(t);
	for (int i = 0; i < 5; ++i) {
		enemys[i] = new Enemy((i+1)*-80, h + i * 50 , FlightType.ENEMY);
	}
}
public void addDiamondEnemy()
{
	float t = random( 60 * 3 ,height - 60 * 3);
	int h = PApplet.parseInt(t);
	int x_axis = 1;
	for (int i = 0; i < 8; ++i) {
		if (i == 0 || i == 7) {
			enemys[i] = new Enemy((x_axis+1)*-80, h, FlightType.ENEMY);
			x_axis++;
		}
		else if (i == 1 || i == 5){
			enemys[i] = new Enemy((x_axis+1)*-80, h + 1 * 40, FlightType.ENEMY);
			enemys[i+1] = new Enemy((x_axis+1)*-80, h - 1 * 40, FlightType.ENEMY);
			i++;
			x_axis++;
			
		}
		else {
			enemys[i] = new Enemy((x_axis+1)*-80, h + 2 * 40, FlightType.ENEMY);
			enemys[i+1] = new Enemy((x_axis+1)*-80, h - 2 * 40, FlightType.ENEMY);
			i++;
			x_axis++;
		}
	}
}
public void addEnemyStrong()
{
	for (int i = 0; i < 5; ++i) {
		enemys[i] = new Enemy(0, 40+ i * 85, FlightType.ENEMYSTRONG);
	}
}
class Fighter{
	PImage fighterImg;
	int x = 0;
	int y = 0;
	int type;
	int speed = 5;
	int hp;
	Fighter(int hp) {
		this.fighterImg = loadImage("img/fighter.png");
		this.x = width - this.fighterImg.width;
		this.y = (height-this.fighterImg.height)/2;
		this.type = FlightType.FIGHTER;
		this.hp = hp;
	}

	public void draw() {
		image(fighterImg, this.x, this.y);

		if (isMovingUp) {
			this.move(Direction.UP);
		}
		if (isMovingDown) {
			this.move(Direction.DOWN);	
		}
		if (isMovingLeft) {
			this.move(Direction.LEFT);
		}
		if (isMovingRight) {
			this.move(Direction.RIGHT);	
		}
	} 
	int tmp = 0;//bullet num
	public void shoot() {
			if(tmp < 5){
				bullets[tmp] = new Bullet(x,y);
				tmp++;
			}else{
				for(int i = 0; i < tmp ; i++){
					if(bullets[i] == null || bullets[i].x < 0){
						bullets[i] = new Bullet(x,y);
						break ;
					}
				}
			}
	}

	public void move(int direct) {
		switch (direct) {
			case Direction.UP:
				if (this.y - speed > 0) {
					this.y-= speed;
				}
				break;
			case Direction.DOWN:
				if (this.y + speed < height - this.fighterImg.height) {
					this.y+= speed;
				}
				break;
			case Direction.LEFT:
				if (this.x - speed > 0) {
					this.x-= speed;
				}
				break;
			case Direction.RIGHT:
				if (this.x + speed < width - this.fighterImg.width) {
					this.x+= speed;
				}
				break;
		}
	}

	public void hpValueChange(int value)
	{
		this.hp += value;
		if (this.hp <=0) {
			state = GameState.END;
			return;
		}
		else if (this.hp >= 100) {
			this.hp = 100;
			return;
		}
		return;
	}
}
public class Flame{
	public int x = 0;
	public int y = 0;
	int startTime;
	int showingImg_num;
	public Flame (int x, int y) {
		this.x = x;
		this.y = y;
		this.showingImg_num = 0;
		this.startTime = millis();
	}

	public int getCurrentImg() {
		if (millis() - this.startTime > 100) {
			return this.showingImg_num++;
		}
		else
		{
			return this.showingImg_num;
		}
	}
}

public class FlameMgr{
	ArrayList<Flame> flames = new ArrayList<Flame>(0);
	PImage flame1;
	PImage flame2;
	PImage flame3;
	PImage flame4;
	PImage flame5;

	public FlameMgr() {
		flame1 = loadImage("img/flame1.png");
		flame2 = loadImage("img/flame2.png");
		flame3 = loadImage("img/flame3.png");
		flame4 = loadImage("img/flame4.png");
		flame5 = loadImage("img/flame5.png");
	}

	public void addFlame(int x, int y)
	{
		flames.add(new Flame(x, y));
	}

	public void draw() {
		ArrayList<Flame> flamesToRemove = new ArrayList<Flame>(0);
		for (int i = 0; i < this.flames.size(); ++i) {
			Flame flame = this.flames.get(i);
			int num = flame.getCurrentImg();
			switch (num) {
				case 0:	
					image(this.flame1, flame.x, flame.y);
					break;
				case 1:	
					image(this.flame2, flame.x, flame.y);
					break;
				case 2:	
					image(this.flame3, flame.x, flame.y);
					break;
				case 3:	
					image(this.flame4, flame.x, flame.y);
					break;
				case 4:	
					image(this.flame5, flame.x, flame.y);
					break;
				case 5:
					flamesToRemove.add(flame);
					break;	

			}
		}

		for (int i = 0; i < flamesToRemove.size(); ++i) {
			flames.remove(flamesToRemove.get(i));
		}
	}

}
class HPDisplay {
	PImage hpUI;

	HPDisplay () {
		this.hpUI = loadImage("img/hp.png");
	}
	public void updateWithFighterHP(int hp)
	{
		fill (255,0,0) ;
		rect(15,10, hp * 2 , 20 ) ;
		image(hpUI,10,10);
	}

}
class Treasure{
	int x = 0;
	int y = 0;
	PImage treasureImg;
	Treasure () {
		this.treasureImg = loadImage("img/treasure.png");
		this.randomPosition();
	}

	public void randomPosition() {
		this.x = PApplet.parseInt(random(0, width - this.treasureImg.width));
		this.y = PApplet.parseInt(random(0, height - this.treasureImg.height));
	}

	public void draw() {
		image(this.treasureImg, this.x, this.y);

		if (isHit(this.x, this.y, this.treasureImg.width, this.treasureImg.height, fighter.x, fighter.y, fighter.fighterImg.width, fighter.fighterImg.height)) {
			fighter.hpValueChange(10);
			this.randomPosition();
		}
	}
}
/* @pjs preload=
"img/bg1.png,
img/bg2.png,
img/end1.png,
img/end2.png,
img/enemy.png,
img/enemy2.png,
img/fighter.png,
img/flame1.png,
img/flame2.png,
img/flame3.png,
img/flame4.png,
img/flame5.png,
img/hp.png,
img/shoot.png,
img/start1.png,
img/start2.png,
img/treasure.png"; */
  public void settings() { 	size(640, 480); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "assign6" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
