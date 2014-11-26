// @formatter:off
define([
    'ui/UIComponent',
    'app/managers/TemplateManager',
    'app/services/UserService',
    'app/utils/CodeUtils',
    'app/utils/RenderUtils'
], function(UIComponent, TemplateManager, UserService, CodeUtils, RenderUtils) {
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

        var fileImage$ = $('#fileImage');

        $('.qsPortraitContainer', view$).on(appRuntime.events.click, function() {
            fileImage$[0].click();
            fileImage$.off().on('change', function() {
                UserService.updatePortrait();
            });
        }.bind(this));
        $('.qsBackgroundContainer', view$).on(appRuntime.events.click, function() {
            fileImage$[0].click();
            fileImage$.off().on('change', function() {
                UserService.updateBackground();
            });
        }.bind(this));

        $('.qsGender', view$).on(appRuntime.events.click, function() {
            appRuntime.view.to('app/views/user/U09Gender');
        }.bind(this));

        $('.qsHairType', view$).on(appRuntime.events.click, function() {
            appRuntime.view.to('app/views/user/U05HairType');
        }.bind(this));

        $('.qsPasswd', view$).on(appRuntime.events.click, function() {
            appRuntime.view.to('app/views/user/U08Passwd');
        }.bind(this));

        $('.qsEmail', view$).on(appRuntime.events.click, function() {
            appRuntime.view.to('app/views/user/U04Email');
        }.bind(this));

        $('.qsHelp', view$).on(appRuntime.events.click, function() {
            appRuntime.view.to('app/views/user/U03Help', {
                "type" : "help"
            });
        }.bind(this));

        $('.qsRegulation', view$).on(appRuntime.events.click, function() {
            appRuntime.view.to('app/views/user/U03Help', {
                "type" : "regulation"
            });
        }.bind(this));

        $('#logout', view$).on(appRuntime.events.click, function() {
            UserService.logout();

            appRuntime.view.to('app/views/show/S01Home');
        }.bind(this));
    };

    UserSettingComponents.prototype.validate = function() {
        var view$ = $('.qsTpltUserSettingMain', this._dom$);

        var name = $("#name", view$).val();
        var age = $("#age", view$).val();
        var height = $("#height", view$).val();
        var weight = $("#weight", view$).val();

        if (name.length == 0) {
            alert("请输入姓名");
            return false;
        }
        if (age.length == 0) {
            alert("请输入年龄");
            return false;
        }
        if (height.length == 0) {
            alert("请输入身高");
            return false;
        }
        if (weight.length == 0) {
            alert("请输入体重");
            return false;
        }

        return true;
    };

    UserSettingComponents.prototype.save = function() {
        var view$ = $('.qsTpltUserSettingMain', this._dom$);
        return {
            "name" : $("#name", view$).val(),
            "age" : $("#age", view$).val(),
            "height" : $("#height", view$).val(),
            "weight" : $("#weight", view$).val()
        };
    };

    return UserSettingComponents;
});
