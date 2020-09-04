package com.hero.game;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Assets {
	File playerFile;
	static TextureAtlas charAtlas;
	static Sprite enemy;
	static Sprite cursor;
	static Sprite redBar, greenBar;
	static TiledMap map;
	static BitmapFont font, menuFont, bigFont;
	static Texture blue, red, statBack, butBack, mainBack;
	static Sprite slash;
	static Sprite butBack2;
	static AssetManager manager;
	static String unitInfo;
	HeroGame game;
	
	public Assets(HeroGame game) {
		this.game = game;
	}
	
	
	public void load(){
		manager = new AssetManager();
		
		//Load all assets
		map = new TmxMapLoader().load("testChar.tmx");
		manager.load("Units.atlas",  TextureAtlas.class);
		manager.load("ShaderRED.png", Texture.class);
		manager.load("Shader.png", Texture.class);	
		manager.load("Cursor.png", Texture.class);
		manager.load("Button.png", Texture.class);
		manager.load("ButBack.png", Texture.class);
		manager.load("SimpleBack.png", Texture.class);
		manager.load("MainBackground.png", Texture.class);
		manager.load("greenBar.png", Texture.class);
		manager.load("redBar.png", Texture.class);
		manager.load("Slash.png", Texture.class);
		manager.finishLoading();
		
		//Player Sprites
		charAtlas = manager.get("Units.atlas",  TextureAtlas.class);
		enemy = charAtlas.createSprite("sprite_sheet-222");
		
		//Health Bars
		redBar = new Sprite(manager.get("redBar.png", Texture.class));
		greenBar = new Sprite(manager.get("greenBar.png", Texture.class));
		
		//Slash Animation
		slash =  new Sprite (manager.get("Slash.png", Texture.class));

		//Movement Shaders
		blue = manager.get("Shader.png", Texture.class);
		red = manager.get("ShaderRED.png", Texture.class);
		
		//Cursor and Buttons	
		cursor = new Sprite(manager.get("Cursor.png", Texture.class));
		butBack = manager.get("Button.png", Texture.class);
		butBack2 = new Sprite(manager.get("ButBack.png", Texture.class));
	
		//Fonts
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("slkscr.ttf"));
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		
		//Main
		param.size = 8;
		param.color = Color.BLACK;
		font = gen.generateFont(param);
		
		//Big
		param.size = 64;
		param.color = Color.BLACK;
		bigFont = gen.generateFont(param);
		
		//Alternative Color
		param.size = 8;
		param.color = Color.PINK;
		menuFont = gen.generateFont(param);
		gen.dispose();
		
		//Backgrounds
		statBack = manager.get("SimpleBack.png", Texture.class);
		mainBack = manager.get("MainBackground.png", Texture.class);
		
		//IO
		playerFile = Gdx.files.internal("PlayerUnits").file();
		}

	public void loadCampaignData() {
		
	}
	
	
	/**
	 * Create a sprite sheet with the given mapID 
	 * @param mapID (an Integer in string format)
	 * @return
	 */
	public Sprite createPlayerMapImage(String mapID) {
		String id = "sprite_sheet-" + mapID;
		Sprite playerSprite = charAtlas.createSprite(id);
		return playerSprite;	
	}
	
	public PlayerUnit constructPlayer(String playerID) {		
		FileHandle file = Gdx.files.local("" + playerID + ".txt");		
		unitInfo = new String(file.readString());
		
		String name = unitInfo.substring(unitInfo.indexOf("Name:") + 5, unitInfo.indexOf("$", unitInfo.indexOf("Name:")));		
		
		//Max Health
		String MaxHealth = unitInfo.substring(unitInfo.indexOf("Health:") + 7, unitInfo.indexOf("$", unitInfo.indexOf("Health:")));		
		int maxHealth = Integer.valueOf(MaxHealth);
		
		//Damage
		String Damage = unitInfo.substring(unitInfo.indexOf("Damage:") + 7, unitInfo.indexOf("$", unitInfo.indexOf("Damage:")));
		int damage = Integer.valueOf(Damage);
		
		//Movement Range
		String Movement = unitInfo.substring(unitInfo.indexOf("Movement:") + 9, unitInfo.indexOf("$", unitInfo.indexOf("Movement:")));
		int movement = Integer.valueOf(Movement);
		
		//Attack Range
		String AttackRange = unitInfo.substring(unitInfo.indexOf("Attack Range:") + 13, unitInfo.indexOf("$", unitInfo.indexOf("Attack Range:")));
		int attackRange = Integer.valueOf(AttackRange);

		//Defense
		String Defense = unitInfo.substring(unitInfo.indexOf("Defense:") + 8, unitInfo.indexOf("$", unitInfo.indexOf("Defense:")));
		int defense = Integer.valueOf(Defense);
		
		//Image ID
		String MapID = unitInfo.substring(unitInfo.indexOf("MapID:") + 6, unitInfo.indexOf("$", unitInfo.indexOf("MapID:")));
		
		PlayerUnit unit = new PlayerUnit(createPlayerMapImage(MapID), name, MapID, playerID, movement, attackRange, maxHealth, damage, defense);
		return unit;
	}
	
	public void writeCampaignData(PlayerUnit[] players) {
		//Write Save Data for players
		for(PlayerUnit player: players) {
			writePlayer(player, player.ID);
		}
	}
	
	public void writePlayer (PlayerUnit player, String playerID) {
		FileHandle file = Gdx.files.local(playerID + ".txt");
		file.writeString("Name:" + player.name + "$", false);
		file.writeString("Health:" + player.maxHealth + "$", true);
		file.writeString("Damage:" + player.damage + "$", true);
		file.writeString("Movement:" + player.movement + "$", true);
		file.writeString("Attack Range:" + player.attackRange + "$", true);
		file.writeString("Defense:" + player.defense + "$", true);
		file.writeString("MapID:" + player.MAPID + "$", true);
	}
	
	public void dispose() {
		manager.dispose();
	}
}
