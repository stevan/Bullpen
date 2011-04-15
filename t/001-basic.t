#!perl

use strict;
use warnings;
use FindBin;

use Test::More;

use ZeroMQ qw[ :all ];

my $coord_addr     = 'tcp://127.0.0.1:6666';
my $publisher_addr = 'tcp://127.0.0.1:7777';

diag "Make sure you have started a worker and server process using the conf/testing/*.json configs";

my $cxt = ZeroMQ::Context->new;

my $coordinator = $cxt->socket(ZMQ_REQ);
$coordinator->connect($coord_addr);

my $subscriber = $cxt->socket(ZMQ_SUB);
$subscriber->connect($publisher_addr);

$coordinator->send('testing');

my $uuid = $coordinator->recv->data;

$subscriber->setsockopt(ZMQ_SUBSCRIBE, $uuid);

my $count = 0;

my $data = $subscriber->recv->data;
$count++;
like($data, qr/^$uuid /, '... got the subscriber ID as expected');
like($data, qr/^$uuid \[$count\]/, '... got the message as expected');
while ( 1 ) {
    $data = $subscriber->recv->data;
    last if $data =~ /^$uuid $/;
    $count++;
    like($data, qr/^$uuid /, '... got the subscriber ID as expected');
    like($data, qr/^$uuid \[$count\]/, '... got the message as expected');
}

is($count, 50, '... got the number of messages we expected');

done_testing;



