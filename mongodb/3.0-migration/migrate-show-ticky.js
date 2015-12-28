db.getCollection('shows').update(
    {'sticky' : null},
    {'$set' : {'sticky' : false}},
    {'multi' : true}
);
