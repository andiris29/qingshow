// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, RenderUtils) {
// @formatter:on
  
    /**
     * User Settings's UI Componets
     */
    var UserSettingComponents = function(dom) {
        UserSettingComponents.superclass.constructor.apply(this, arguments);

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/user-setting.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        }.bind(this), function(callback) {
            // Load data
            DataService.request('/feeding/byPeople', {
                'peopleId' : 1
            }, callback);
        }.bind(this)], function(err, results) {
            this._render(results[1]);
        }.bind(this));
      
    };
    andrea.oo.extend(UserSettingComponents, UIComponent);

    UserSettingComponents.prototype._render = function(response) {
        UserSettingComponents.superclass._render.apply(this, arguments);

        var view$ = $('.qsTpltUserSettingMain', this._dom$);
        var people = response.data.people;

        // Data Render
        $('.qsPortrait', view$).css('background-image', RenderUtils.imagePathToBackground(people.portrait));
        $('.qsThumbnail', view$).css('background-image', RenderUtils.imagePathToBackground(people.thumbnail));
        $('#name', view$).attr('value', people.name);
        $('#gender', view$).attr('value', people.gender);
        $('#age', view$).attr('value', people.age);
        $('#height', view$).attr('value', people.height);
        $('#weight', view$).attr('value', people.weight);

        var hairType = RenderUtils.hairTypeCodesToValue(people.hairType);

        $('#hairType', view$).attr('value', hairType);

        $('.qsHairType', view$).on('click', function() {
            // TODO EVENT GOTO HairType
            console.log('Goto HairType');
            //appRuntime.view.to(S03Show, show);
        }.bind(this));
    };

    UserSettingComponents.prototype._save = function() {
    };

    return UserSettingComponents;
});
