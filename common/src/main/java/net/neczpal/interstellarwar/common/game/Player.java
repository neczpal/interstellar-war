package net.neczpal.interstellarwar.common.game;


public class Player {
    private int mRoomIndex;
//    private Color mColor;//????

    //amount of current money
    private int mMoney;

    //ATK - indices
    private int mSpaceShipCountLevel;// 1     ->    2     ->   4
    private int mSpaceShipSpeedLevel;// 0.35  ->    0.75  ->   1.5
    private int mSpaceShipAttackLevel;//1.    ->    1.2   ->   1.5

    //DEF - indices
    private int mShieldMaxLevel;//       30   ->    50    -> 100
    private int mShieldSpeedLevel;//      1.  ->    1.2   ->   1.5
    private int mShieldCostLevel;//       15  ->    12    ->   8

    //TECH - indices
    private int mUnitRateLevel;//1.    ->    1.2   ->   1.5
    private int mMoneyRateLevel;//1.    ->    1.2   ->   1.5
    private int mUpgradeCostLevel;//1.    ->    0.8   ->   0.6

    public Player (int roomIndex) {
        mRoomIndex = roomIndex;

        mMoney = 0;

        mSpaceShipCountLevel = 1;
        mSpaceShipSpeedLevel = 1;
        mSpaceShipAttackLevel = 1;

        mShieldMaxLevel = 1;
        mShieldSpeedLevel = 1;
        mShieldCostLevel = 1;

        mUnitRateLevel = 1;
        mMoneyRateLevel = 1;
        mUpgradeCostLevel = 1;

    }

}
