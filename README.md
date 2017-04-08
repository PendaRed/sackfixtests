<div style="vertical-align:center">
  <img src ="http://www.sackfix.org/assets/sf_logo.png" />
  <img src ="http://www.sackfix.org/assets/sackfix.png" />
</div>

# Sack Fix tests

SackFixTests is the independent test suite used to validate the session layer implementation within SackFix.
 
It does not rely on SackFix itself (see [sackfix.org](www.sackfix.org)) but includes a very tiny set of
utility classes to act as a Fix initiator.

This means you can use it to validate your own session layer implementation.

Start up your Fix server implementation, change the src/main/resources/application.conf so that it matches your server 
settings.  Then fire up sbt and run the tests.

## Versions

JDK 1.8, Scala 2.11.

## Does your client need to do anything special?

The tests require that should you receive a NewOrderSingle then you should reply with an
ExecutionReport.   For instance, see T8_ResendRequestSpec.

You also need a persistent message store so that replay of business messages works.

## What is not tested?

Well, SackFix will not implement login, or encryption, or data content.  It will also not insist on the ordering of 
fields within the message or header as experience shows there are too many implementations which do not do that 
correctly.

Due to the above, tests that focus on these items were not written.

In particular:  
- A tests of ordering of fields in a group, 
- A test that all header fields preceed all body fields

## What is tested?

Pretty much all the session level validation and message flows for login, logout and resend requests.

## How does it work?

There is a really straight forward Fix sender and receiver based on the old Java sockets - this is very deliberately not
straying into NIO, Netty or Akka.io etc.  Its as simple as I could do it, with little thought or effort.

So the simple fix package does key value tuples, and sockets.  It provides the handy utils for logging in, logging out
and creating headers using the values from application.conf.

## Why SackFix tests?

As I said, I've been learning Scala and wanted to know the session level spec, and writing the tests seemed like a fine
way to learn.

All the best,

Jonathan

2017

## How to run it

<pre>
cd into the base dir of the project
sbt
>clean
>compile
>test
</pre>

### Expected output

<pre>
> test
[info] Compiling 39 Scala sources to C:\all_dev\sackfix\sackfixtests\sf-fix-tester\target\scala-2.11\test-classes...
[info] T14_h_DuplicateTag:
[info] Receive application or administrative message
[info] - should Send message with missing value
[info] T4_b_SendThemTestReqSpec:
[info] Send heartbeat message
[info] - should If send a test request expect heartbeat reply
[info] T2_q_InvalidMessageType:
[info] Receive Message Standard Header
[info] - should Reject invalid message type
[info] T10_2_ReceiveSeqResetSeqGapFillDupLowerSpec:
[info] Receive SequenceReset Message
[info] - should If msgSeq<expected and poss dup ignore
[info] T2_d_GarbledMessage:
[info] Receive Message Standard Header
[info] - should Garbled message ignored
[info] T2_t_BadHeaderOrder:
[info] Receive Message Standard Header
[info] - should Reject a message where first three tags are not as expected
[info] T1S_d_BadLoginMsgSpec:
[info] Receive Logon message
[info] - should send a logout and close socket
[info] SackFixTestSpec:
Very slow test, waiting 22 seconds for heartbeat to arrive
Very slow test, waiting another 6 seconds for testreq to arrive
[info] T6_a_NothingReceivedForHeartbeatPlus20Spec:
[info] Send test request
[info] - should No data received during heartbeat interval
[info] T1B_c_ExpectLoginResponseSpec:
[info] Connect and send logon message
[info] - should Valid Logon message as response is received
[info] T1B_e_FirstMessageNotLoginSpec:
[info] Connect and send logon message
[info] - should Receive any message other than a Logon message
[info] T11_c_ReceiveSeqResetSeqTooLowSpec:
[info] Receive SequenceReset Message
[info] - should Reset the sequence number!
Very slow test, waiting 22 seconds for heartbeat to arrive
[info] T4_a_NoHeartbeatInIntervalSpec:
[info] Send heartbeat message
[info] - should If nothing sent for heartbeat then expect them to send one
[info] T2_e_SeqNumLowDupFlagGoodTimes:
[info] Receive Message Standard Header
[info] - should Message has low seq num and poss dup flag=y with correct times, ignore it as dup.
[info] T14_d_MissingValue:
[info] Receive application or administrative message
[info] - should Send message with missing value
[info] T2_i_BadBeginString:
[info] Receive Message Standard Header
[info] - should Bad begin Str, so logout
[info] - should Bad begin Str and close socket after 2 secs
[info] T14_i_RepeatingGroupCount:
[info] Receive application or administrative message
[info] - should Send message with bad repeating group count
[info] T2_o_SendingTimeOut:
[info] Receive Message Standard Header
[info] - should check the clock is within 2 mins
[info] T13_b_LogoutFromClient:
[info] Receive Logout message
[info] - should ClientLogoutSequence
[info] T1B_d4_BadCompIdSpec:
[info] Connect and send logon message
[info] - should Invalid Logon message is received - BadCompIds Close the socket
[info] T2_g_PossDupFlagMissingOrigSendingTime:
[info] Receive Message Standard Header
[info] - should PossibleDupFlag=Y but origSendingTime is missing
[info] T8_ResendRequestSpec:
[info] Receive Reject Message
[info] - should Send them a valid reject message
[info] T2_f_SeqNumLowDupFlagBadTimes:
[info] Receive Message Standard Header
[info] - should a message has low seq num and poss dup flag=y, and origsendtime>send time, disconnect
[info] - should logon has low seq num and poss dup flag=y, disconnect and close socket after 2 secs
[info] T10_4_ReceiveSeqResetSeqGapFillSpec:
[info] Receive SequenceReset Message
[info] - should Reset the sequence number!
[info] T10_3_ReceiveSeqResetSeqGapFillLowerSpec:
[info] Receive SequenceReset Message
[info] - should If msgSeq<expected and NOT poss dup disconnect
[info] T14_f_ValueBadFormat:
[info] Receive application or administrative message
[info] - should Send message with missing value
[info] T7_RejectHandledSpec:
[info] Receive Reject Message
[info] - should Send them a valid reject message
[info] T2_m_GarbledBodyLen:
[info] Receive Message Standard Header
[info] - should Bad compIds, so reject and logout
[info] T3_b_GarbledChecksum:
[info] Receive Message Standard Trailer
[info] - should Garbled message ignored
[info] T11_a_ReceiveSeqResetSeqSpec:
[info] Receive SequenceReset Message
[info] - should Reset the sequence number!
[info] T2_k_BadCompId:
[info] Receive Message Standard Header
[info] - should Bad compIds, so reject and logout
[info] T2_c_SeqNumLowDupFlag:
[info] Receive Message Standard Header
[info] - should logon has low seq num and poss dup flag=y, disconnect
[info] T10_1_ReceiveSeqResetSeqGapFillHigherSeqNumSpec:
[info] Receive SequenceReset Message
[info] - should If msgSeq>expected then gap fill
[info] T2_b_SeqNumResend:
[info] Receive Message Standard Header
[info] - should accept first message and then resend request
[info] T1S_a_LoginResendRequestSpec:
[info] Receive Logon message
[info] - should Receive a logon and a resend request
[info] T1S_c_BadCompIdSpec:
[info] Receive Logon message
[info] - should Close the socket
[info] T1S_b_LoginTwiceSpec:
[info] Receive Logon message
[info] - should 2nd Logon should just get socket closed
[info] T14_b_MissingMandTag:
[info] Receive application or administrative message
[info] - should Send message with missing mand tag
[info] T3_e_GarbledChecksumBadLen:
[info] Receive Message Standard Trailer
[info] - should Garbled checsum of 2 chars ignored
[info] Run completed in 1 minute, 32 seconds.
[info] Total number of tests run: 40
[info] Suites: completed 39, aborted 0
[info] Tests: succeeded 40, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 99 s, completed
</pre>
