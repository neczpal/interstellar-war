//
//  Road.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation

public class Road {
    private var mFrom : Planet, mTo : Planet;

    init(from : Planet, to : Planet) {
        mFrom = from;
        mTo = to;
    }

    // GETTERS

    public func getFrom () -> Planet {
        return mFrom;
    }

    public func getTo () -> Planet {
        return mTo;
    }

}
