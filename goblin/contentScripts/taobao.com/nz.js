// http://nz.taobao.com/
var api = new RuntimeAPI({
    'crawl' : function() {
        var subURLs = [];

        $('dl', $('.fix-box')).each(function(index, dl$) {
            // Main
            $('.cat-title', dl$).each(function(index, categoryTitle) {
                subURLs.push($('a', categoryTitle).attr('href'));
            });
            // Hover
            $('textarea', dl$).filter(function(index, textarea) {
                return $(textarea).hasClass('J_ext_data');
            }).each(function(index, textarea) {
                var categories = eval($(textarea).val().trim());
                categories.forEach(function(category, index) {
                    if (category.cat_href) {
                        subURLs.push(category.cat_href);
                    }
                });
            });
        });
        // Remove duplicated
        subURLs = subURLs.filter(function(element, index) {
            return subURLs.indexOf(element) === index;
        });

        // Create sub crawers
        api.createSubCrawers(subURLs);
    }
});
