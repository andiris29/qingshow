define([
    'main/core/model',
    'main/views/View'
],function (
    model,
    View
){
    violet.ui.factory.registerDependencies('main/views/P06BonusForge');

    var P06BonusForge = function (dom, initOptions) {
        P06BonusForge.superclass.constructor.apply(this, arguments);
        var button$ = $('button', this._dom);
        button$.on('click',this._submit.bind(this));
    };

    violet.oo.extend(P06BonusForge, View);

    P06BonusForge.prototype._submit = function(){
        var peopleRef = $('#peopleRef', this._dom).val();
        var itemRef = $('#itemRef', this._dom).val();
        var actualPrice = $('#actualPrice', this._dom).val();

        if (!peopleRef) {
            alertify.error('请输入用户id');
            return;
        }

        if (!itemRef) {
            alertify.error('请输入商品id');
            return;
        }

        if (!actualPrice) {
            alertify.error('请输入价格');
            return;
        }

        this.request('/userBonus/forge','post',{
            'itemRef' : itemRef,
            'fakeTrade' : {
                'promoterRef' : peopleRef,
                'actualPrice' : actualPrice
            }
        }, function(err, metadata, data){
            if(err){
                alertify.error('提交失败');
                return;
            }
            if(metadata.error){
                if(metadata.error == '1008'){
                    alertify.error('价格超出原价');
                }else {
                    alertify.error('提交失败');
                }
                return;
            }

            alertify.success('提交成功');

        }.bind(this));
    };

    return P06BonusForge;
});