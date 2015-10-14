var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var sharedObjectSchema = {
	type : Number,
	initiatorRef : {
		type : Schema.Types.ObjectId,
		ref : 'peoples'
	},
	targetInfo : {
		bonus : {
			ownerRef : {
				type : Schema.Types.ObjectId,
        		ref : 'peoples'
			},
			total : Number,
			withdrawTotal : Number
		},
		show : {
			_id : {
				type : Schema.Types.ObjectId,
				ref : 'shows'
			},
			cover : String,
			coverForeground : String
		},
		trade : {
			_id : {
				type : Schema.Types.ObjectId,
				ref : 'trades'
			},
			itemSnapshot : {
				name : String,
				promoPrice : String,
			},
			totalFee : Number,
			quantity : Number
		}
	}
}

var SharedObject = mongoose.model('sharedObjects', sharedObjectSchema);

module.exports = SharedObject;