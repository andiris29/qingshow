//
//  QSG01ItemWebViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 1/12/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS10ItemDetailViewController : UIViewController<UIAlertViewDelegate, UIWebViewDelegate>

- (id)initWithItem:(NSDictionary*)item;

@property (weak, nonatomic) IBOutlet UIWebView *webView;

@end
