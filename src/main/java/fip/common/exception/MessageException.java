package fip.common.exception;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-12-30
 * Time: 10:13:13
 * To change this template use File | Settings | File Templates.
 */

import fip.common.utils.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageException extends RuntimeException
{
  private final List messageList;

  public MessageException()
  {
    this.messageList = Collections.EMPTY_LIST;
  }

  //TODO: ÐÂÔöBUSSINESS MESSAGE  for batch
  public MessageException(Message message)
  {
    super(message.toString());
    List list = new ArrayList();
    list.add(message);
    this.messageList = Collections.unmodifiableList(list);
  }

  public MessageException(List messageList)
  {
    super(messageList.toString());
    List list = new ArrayList(messageList);
    this.messageList = Collections.unmodifiableList(list);
  }

  public List getMessageList()
  {
    return this.messageList;
  }
}