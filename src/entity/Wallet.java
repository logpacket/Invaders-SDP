package entity;

import engine.Core;
import engine.FileManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

public class Wallet {
    private static Logger logger = Core.getLogger();
    private int coin;

    //bullet speed level
    private int bulletLevel;

    //shot frequency level
    private int shotLevel;

    //additional lives level
    private int livesLevel;

    //coin gain level
    private int coinLevel;

    public Wallet()
    {
        this.coin = 0;
        this.bulletLevel = 1;
        this.shotLevel = 1;
        this.livesLevel = 1;
        this.coinLevel = 1;
        writeWallet();
    }

    public Wallet(int coin, int bulletLevel, int shotLevel, int livesLevel, int coinLevel)
    {
        this.coin = coin;
        this.bulletLevel = bulletLevel;
        this.shotLevel = shotLevel;
        this.livesLevel = livesLevel;
        this.coinLevel = coinLevel;
    }

    public int getCoin()
    {
        return coin;
    }

    public int getBulletLevel()
    {
        return bulletLevel;
    }

    public int getShotLevel()
    {
        return shotLevel;
    }

    public int getLivesLevel() { return livesLevel; }

    public int getCoinLevel()
    {
        return coinLevel;
    }

    public void setBulletLevel(int bulletLevel)
    {
        this.bulletLevel = bulletLevel;
        writeWallet();
        logger.info("Upgrade Bullet Speed " + (bulletLevel -1) + "to " + bulletLevel);
    }

    public void setShotLevel(int shotLevel)
    {
        this.shotLevel = shotLevel;
        writeWallet();
        logger.info("Upgrade Shop Frequency  " + (shotLevel -1) + "to " + shotLevel);
    }

    public void setLivesLevel(int livesLevel)
    {
        this.livesLevel = livesLevel;
        writeWallet();
        logger.info("Upgrade Additional Lives " + (livesLevel -1) + "to " + livesLevel);
    }

    public void setCoinLevel(int coinLevel)
    {
        this.coinLevel = coinLevel;
        writeWallet();
        logger.info("Upgrade Gain Coin " + (coinLevel -1) + "to " + coinLevel);
    }

    public void deposit(int amount)
    {
        if(amount <= 0) return;
        coin += amount;
        writeWallet();
        logger.info("Deposit completed. Your coin: " + this.coin);
    }

    public boolean withdraw(int amount)
    {
        if(amount <= 0) return false;
        if(coin - amount < 0)
        {
            logger.info("Insufficient coin");
            return false;
        }
        coin -= amount;
        writeWallet();
        logger.info("Withdraw completed. Your coin: " + this.coin);
        return true;
    }

    //현재 지갑상태를 파일에 저장. 저장방식: coin, bulletLevel, shotLevel, livesLevel, coinLevel 순으로 한줄씩 저장
    //Save the current wallet state to a file. Save format: coin, bulletLevel, shotLevel, livesLevel, coinLevel, each in one line
    private void writeWallet()
    {
        try {
            FileManager.getInstance().saveWallet(this);
        } catch (IOException e) {
            logger.warning("Couldn't load wallet!");
        }
    }

    // 파일에 적힌 정보로 지갑 생성. 만약 파일이 손상되어 읽을 수 없다면 초기값(0)으로 생성하기
    //Create a wallet using the information written in the file. If the file is damaged or unreadable, create it with initial values (0)
    public static Wallet getWallet() {
        BufferedReader bufferedReader = null;

        try {
            //FileManager 를 통해 파일에서 지갑 데이터를 불러옴
            //Load wallet data from the file via FileManager
            bufferedReader = FileManager.getInstance().loadWallet();

            if (bufferedReader == null) {
                logger.info("Wallet file does not exist, initializing with default values.");

                return new Wallet();
            }

            //파일에서 각 줄을 읽어와서 값 설정
            //Read each line from the file and set the values
            int coin = Integer.parseInt(bufferedReader.readLine());
            int[] levelSeq = new int[4]; //bulletLevel, shotLevel, livesLevel, coinLevel
            for (int i = 0; i < 4; i++) {
                int level = Integer.parseInt(bufferedReader.readLine());
                if(level > 4 || level <= 0){
                    logger.info("Weird level. Initializing with default values.");
                    return new Wallet();
                }
                levelSeq[i] = level;
            }

            return new Wallet(coin, levelSeq[0], levelSeq[1], levelSeq[2], levelSeq[3]);

        } catch (IOException | NumberFormatException e) {
            //파일을 읽지 못하거나 손상된 경우 기본값으로 반환
            //If there is an error reading the file or if it's corrupted, return with default values
            logger.info("Error loading wallet data. Initializing with default values.");
            return new Wallet();
        } finally {
            //파일 리소스 해제
            //Release file resources
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.warning("Couldn't close file.");
                }
            }
        }
    }

}