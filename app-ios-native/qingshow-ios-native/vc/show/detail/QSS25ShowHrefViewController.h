//
//  QSS25ShowHrefViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/29.
//  Copyright © 2015年 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface QSS25ShowHrefViewController : UIViewController<UIWebViewDelegate>

@property (weak, nonatomic) IBOutlet UIWebView *webView;

- (instancetype)initWithShow:(NSDictionary*)showDict;

@end
