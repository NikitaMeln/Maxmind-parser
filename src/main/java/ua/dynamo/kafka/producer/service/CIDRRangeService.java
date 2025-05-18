package ua.dynamo.kafka.producer.service;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.dynamo.kafka.producer.exception.CIDRRangeException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class CIDRRangeService {

    private static final Logger logger = LoggerFactory.getLogger(CIDRRangeService.class);

    public boolean isIpInRange(String cidr, String ipAddress) {
        try {
            IPAddressString cidrAddressString = new IPAddressString(cidr);
            IPAddress cidrAddress = cidrAddressString.toAddress();

            IPAddressString ipAddressString = new IPAddressString(ipAddress);
            IPAddress userAddress = ipAddressString.toAddress();

            return cidrAddress.contains(userAddress);
        } catch (Exception e) {
            logger.error("Error: Invalid CIDR or IP address format: {}", e.getMessage());
            throw new CIDRRangeException(e.getMessage());
        }
    }

    public List<String> getIpRange(String ip) {
        IPAddressString ipAddressString = new IPAddressString(ip);

        if (!ipAddressString.isValid()) {
            logger.error("Invalid IP-address: {}", ip);
            throw new CIDRRangeException("Invalid IP-address: " + ip);
        }

        try {
            IPAddress ipAddress = ipAddressString.toAddress();

            if (ipAddress.isIPv4()) {
                return ipv4ToCidrRange(ip);
            } else if (ipAddress.isIPv6()) {
                return ipv6ToCidrRange(ip);
            } else {
                throw new CIDRRangeException("Unsupported IP-address format: " + ip);
            }
        } catch (Exception e) {
            throw new CIDRRangeException("Error processing IP-address: " + e.getMessage());
        }
    }

    private long ipv4ToInt(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            byte[] octets = inetAddress.getAddress();
            long result = 0;
            for (byte octet : octets) {
                result = (result << 8) | (octet & 0xFF);
            }
            return result;
        } catch (UnknownHostException e) {
            throw new CIDRRangeException("Error processing IP-address: " + e.getMessage());
        }
    }

    private String intToIpv4(long intIp) {
        byte[] octets = new byte[] {
                (byte) ((intIp >> 24) & 0xFF),
                (byte) ((intIp >> 16) & 0xFF),
                (byte) ((intIp >> 8) & 0xFF),
                (byte) (intIp & 0xFF)
        };
        try {
            InetAddress inetAddress = InetAddress.getByAddress(octets);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            throw new CIDRRangeException("Error processing IP-address: " + e.getMessage());
        }
    }

    private long[] ipv6ToInt(String ip) {
        try {
            byte[] octets = InetAddress.getByName(ip).getAddress();
            if (octets.length != 16) {
                throw new CIDRRangeException("Invalid IPv6 address format: Expected 16 bytes.");
            }

            long highBits = 0;
            long lowBits = 0;
            for (int i = 0; i < 8; i++) {
                highBits = (highBits << 8) | (octets[i] & 0xFF);
            }
            for (int i = 8; i < 16; i++) {
                lowBits = (lowBits << 8) | (octets[i] & 0xFF);
            }
            return new long[] { highBits, lowBits };
        } catch (UnknownHostException e) {
            throw new CIDRRangeException("Invalid IPv6 address: Unable to resolve the host.", e);
        }
    }

    private String intToIpv6(long[] intIp) {
        byte[] octets = new byte[16];
        for (int i = 0; i < 8; i++) {
            octets[i] = (byte) ((intIp[0] >> (56 - i * 8)) & 0xFF);
        }
        for (int i = 0; i < 8; i++) {
            octets[i + 8] = (byte) ((intIp[1] >> (56 - i * 8)) & 0xFF);
        }
        try {
            InetAddress inetAddress = InetAddress.getByAddress(octets);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            throw new CIDRRangeException("Invalid IPv6 address: Unable to resolve the host.", e);
        }
    }

    private List<String> ipv4ToCidrRange(String ipv4Address) {
        long intIp = ipv4ToInt(ipv4Address);
        List<String> possibleCidrs = new ArrayList<>();
        for (int prefix = 32; prefix >= 8; prefix--) {
            int hostBits = 32 - prefix;
            long cidrRangeStart = intIp & ~((1L << hostBits) - 1);
            String cidrString = intToIpv4(cidrRangeStart) + "/" + prefix;
            possibleCidrs.add(cidrString);
        }
        return possibleCidrs;
    }

    private List<String> ipv6ToCidrRange(String ipv6Address) {
        long[] intIp = ipv6ToInt(ipv6Address);
        List<String> possibleCidrs = new ArrayList<>();
        for (int prefix = 64; prefix >= 0; prefix--) {
            long[] cidrStart = calculateCidrRange(intIp, prefix);
            String cidrString = intToIpv6(cidrStart) + "/" + prefix;
            possibleCidrs.add(compressedV6(cidrString));
        }
        return possibleCidrs;
    }

    private String compressedV6(String cidrString) {
        IPAddressString ipAddressString = new IPAddressString(cidrString);
        try {
            IPAddress ipAddress = ipAddressString.toAddress();
            return ipAddress.toCompressedString();
        } catch (Exception e) {
            throw new CIDRRangeException("Error: Invalid CIDR: " + e.getMessage());
        }
    }

    private long[] calculateCidrRange(long[] intIp, int prefix) {
        long[] result = new long[2];
        if (prefix > 64) {
            result[0] = intIp[0];
            long mask = ~((1L << (128 - prefix)) - 1);
            result[1] = intIp[1] & mask;
        } else if (prefix == 64) {
            result[0] = intIp[0];
            result[1] = 0;
        } else {
            long mask = ~((1L << (64 - prefix)) - 1);
            result[0] = intIp[0] & mask;
            result[1] = 0;
        }
        return result;
    }
}
