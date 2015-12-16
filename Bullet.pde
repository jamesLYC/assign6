class Bullet{
	PImage bullet;
	int x = 0;
	int y = 0;
	Bullet(int x, int y) {
		this.x = x;
		this.y = y;
		this.bullet = loadImage("img/shoot.png");
	}

	void draw()
	{
		image(bullet, x, y);
	}
	void move(){
		this.x -= 3;	
	}

	boolean isCollideWithEnemy(Enemy enemys)
	{
		if (isHit(this.x, this.y, this.bullet.width, this.bullet.height, enemys.x, enemys.y, enemys.enemyImg.width, enemys.enemyImg.height)) {
		return true;
		}
		return false;
	}
}