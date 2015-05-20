//
//  QSCommentListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSCommentListTableViewProvider.h"

@interface QSS04CommentListViewController : UIViewController<QSCommentListTableViewProviderDelegate, UITextFieldDelegate, UIActionSheetDelegate, UIGestureRecognizerDelegate>

@property (weak, nonatomic) IBOutlet UIView *commentContainer;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *commentBottomConstrain;
@property (weak, nonatomic) IBOutlet UITextField *textField;
@property (weak, nonatomic) IBOutlet UIImageView *headIcon;
@property (weak, nonatomic) IBOutlet UIButton *sendBtn;

- (IBAction)sendBtnPressed:(id)sender;

- (id)initWithShow:(NSDictionary*)showDict;

@end
