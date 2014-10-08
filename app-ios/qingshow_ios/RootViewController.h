//
//  ViewController.h
//  qingshow_ios
//
//  Created by wxy325 on 10/2/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RootViewController : UIViewController<UIWebViewDelegate>

@property (weak, nonatomic) IBOutlet UIWebView *rootWebView;

@end

