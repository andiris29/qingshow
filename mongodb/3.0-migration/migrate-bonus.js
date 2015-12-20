var peoples = db.getCollection('peoples').find({'bonuses' : {'$ne' : null}});

peoples.forEach(function(people) {
    var legacyBonuses = people.bonuses || [],
        bonuses = [];
    legacyBonuses.forEach(function(legacyBonus) {
        var bonus = {
            'migrate' : '2.x.x',
            'ownerRef' : people._id,
            'type' : 0, // Hardcode 0 as trade bonus
            'status' : legacyBonus.status,
            'amount' : legacyBonus.money,
            'description' : legacyBonus.notes,
            'create' : legacyBonus.create
        }
        if (legacyBonus.participants) {
            bonus.participants = legacyBonus.participants;
        }
        if (legacyBonus.trigger) {
            bonus.legacyTrigger = legacyBonus.trigger;
            if (legacyBonus.trigger.tradeRef) {
                bonus.trigger = {
                    'tradeRef' : legacyBonus.trigger.tradeRef
                };
            }
        }
        bonuses.push(bonus);
    });
    if (bonuses.length) {
        db.getCollection('bonuses').insert(bonuses);
    }
    db.getCollection('peoples').update(
        {'_id' : people._id},
        {'$unset' : {'bonuses' : ''}}
    );
});
