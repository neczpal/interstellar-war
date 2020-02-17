package net.neczpal.interstellarwar.common.game;

public class Research {
    private static Research single_instance = null;

    // VALUES - START

    //ATK
    private final int[] mSpaceShipCount;// 1     ->    2     ->   4
    private final double[] mSpaceShipSpeed;// 0.35  ->    0.75  ->   1.5
    private final double[] mSpaceShipAttack;//1.    ->    1.2   ->   1.5

    //DEF
    private final int[] mShieldMax;//       30   ->    50    -> 100
    private final double[] mShieldSpeed;//      1.  ->    1.2   ->   1.5
    private final int[] mShieldCost;//       15  ->    12    ->   8

    //TECH
    private final double[] mUnitRate;//       1.    ->    1.2   ->   1.5
    private final double[] mMoneyRate;//      1.    ->    1.2   ->   1.5
    private final double[] mUpgradeCost;//    1.    ->    0.8   ->   0.6

    //VALUES - END
    private final int[] mCost;//#TODO Cost according to upgrade
//    //COSTS - START
//
//    //ATK
//    private final int[]    mSpaceShipCount;// 1     ->    2     ->   4
//    private final double[] mSpaceShipSpeed;// 0.35  ->    0.75  ->   1.5
//    private final double[] mSpaceShipAttack;//1.    ->    1.2   ->   1.5
//
//    //DEF
//    private final int[]    mShieldMax;//       30   ->    50    -> 100
//    private final double[] mShieldSpeed;//      1.  ->    1.2   ->   1.5
//    private final int[]    mShieldCost;//       15  ->    12    ->   8
//
//    //TECH
//    private final double[] mUnitRate;//       1.    ->    1.2   ->   1.5
//    private final double[] mMoneyRate;//      1.    ->    1.2   ->   1.5
//    private final double[] mUpgradeCost;//    1.    ->    0.8   ->   0.6
//
//    //COSTS - END

    private Research () {
        mSpaceShipCount = new int[] {1, 2, 4};
        mSpaceShipSpeed = new double[] {.35, .75, 1.5};
        mSpaceShipAttack = new double[] {1.0, 1.2, 1.5};

        mShieldMax = new int[] {30, 50, 100};
        mShieldSpeed = new double[] {1.0, 1.2, 1.5};
        mShieldCost = new int[] {15, 12, 8};

        mUnitRate = new double[] {1.0, 1.2, 1.5};
        mMoneyRate = new double[] {1.0, 1.2, 1.5};
        mUpgradeCost = new double[] {1.0, 0.8, 0.6};

        mCost = new int[] {0, 250, 750};
    }

    public static Research getInstance () {
        if (single_instance == null)
            single_instance = new Research ();

        return single_instance;
    }

}
