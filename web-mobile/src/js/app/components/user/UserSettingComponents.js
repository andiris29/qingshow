// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/DataService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, DataService, CodeUtils, RenderUtils) {
// @formatter:on
  
    /**
     * User Settings's UI Componets
     */
    var U05HairType;
    var UserSettingComponents = function(dom) {
        UserSettingComponents.superclass.constructor.apply(this, arguments);

        require(['app/views/user/U05HairType'], function(hairType) {
            U05HairType = hairType;
        });

        async.parallel([ function(callback) {
            // load template
            TemplateManager.load('user/user-setting.html', true, function(err, content$) {
                this._dom$.append(content$);
                callback(null);
            }.bind(this));
        //}.bind(this), function(callback) {
        //    // Load data
        //    DataService.request('/feeding/byPeople', {
        //        'peopleId' : 1
        //    }, callback);
        }.bind(this)], function(err, results) {
            this._render();
        }.bind(this));
      
    };
    andrea.oo.extend(UserSettingComponents, UIComponent);

    UserSettingComponents.prototype._render = function() {
        UserSettingComponents.superclass._render.apply(this, arguments);

        var view$ = $('.qsTpltUserSettingMain', this._dom$);

        var people = this._data._user;

        // Data Render
        $('.qsPortrait', view$).css('background-image', RenderUtils.imagePathToBackground(people.portrait));
        $('.qsThumbnail', view$).css('background-image', RenderUtils.imagePathToBackground(people.thumbnail));
        $('#name', view$).attr('value', people.name);
        $('#gender', view$).attr('value', people.gender);
        $('#age', view$).attr('value', people.age);
        $('#height', view$).attr('value', people.height);
        $('#weight', view$).attr('value', people.weight);

        $('.qsHairType', view$).on('click', function() {
            appRuntime.view.to(U05HairType);
        }.bind(this));
    };

    UserSettingComponents.prototype._save = function() {
    };

    return UserSettingComponents;
});
