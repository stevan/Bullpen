#!perl

use 5.10.0;

use strict;
use warnings;

use ZeroMQ      qw[ :all ];
use Time::HiRes qw[ gettimeofday tv_interval ];

my $base_url = shift || '127.0.0.1';

my $coord_addr     = 'tcp://' . $base_url . ':6666';
my $publisher_addr = 'tcp://' . $base_url . ':7777';

my $cxt = ZeroMQ::Context->new;

say "welcome to my client ...";

my $coordinator = $cxt->socket(ZMQ_REQ);
$coordinator->connect($coord_addr);

say "coordinator connected to $coord_addr";

my $subscriber = $cxt->socket(ZMQ_SUB);
$subscriber->connect($publisher_addr);

say "subscriber bound to $publisher_addr";

while (1) {
    print "? ";
    my $input = <STDIN>;
    chomp $input;

    say "=> sending input=($input) to server";
    $coordinator->send($input);

    my $uuid = $coordinator->recv->data;

    say "<= got back " . $uuid . " from server";

    $subscriber->setsockopt(ZMQ_SUBSCRIBE, $uuid);

    say "starting subscription ...";

    my $start = [gettimeofday];
    my $count = 0;

    my $data = $subscriber->recv->data;
    say "got $data";
    $data =~ s/^$uuid //;
    while ( $data ) {
        $count++;
        $data = $subscriber->recv->data;
        say "got $data";
        $data =~ s/^$uuid //;
    }

    my $elapsed = tv_interval($start, [gettimeofday]);

    say "Fetching $count results took $elapsed";
}