//
//  QSCommentListViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSCommentListTableViewProvider.h"

typedef NS_ENUM(int, QSCommentListViewControllerType) {
    QSCommentListViewControllerTypeShow,
    QSCommentListViewControllerTypePreview
};

@interface QSS04CommentListViewController : UIViewController<QSCommentListTableViewProviderDelegate, UITextFieldDelegate, UIActionSheetDelegate>

@property (weak, nonatomic) IBOutlet UIView *commentContainer;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *commentBottomConstrain;
@property (weak, nonatomic) IBOutlet UITextField *textField;
@property (weak, nonatomic) IBOutlet UIImageView *headIcon;
@property (weak, nonatomic) IBOutlet UIButton *sendBtn;

- (IBAction)sendBtnPressed:(id)sender;

- (id)initWithShow:(NSDictionary*)showDict;
- (id)initWithPreview:(NSDictionary*)previewDict;

@end
