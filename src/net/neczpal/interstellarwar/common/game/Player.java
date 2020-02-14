package net.neczpal.interstellarwar.common.game;

import net.neczpal.interstellarwar.desktop.geom.Color;

public class Player {
    private int mRoomIndex;
    private Color mColor;

    private int mMoney;

    private Research mResearch;

    //ATK
    private int mSpaceShipCount;
    private int mSpaceShipSpeed;
    private int mSpaceShipAttack;

    //DEF
    private int mShieldMax;
    private int mShieldSpeed;
    private int mShieldCost;

    //TECH
    private int mUnitRate;
    private int mMoneyRate;
    private int mUpgradeCost;


}
