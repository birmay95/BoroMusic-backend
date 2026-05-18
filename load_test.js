import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '2m', target: 50 },
        { duration: '5m', target: 50 },
        { duration: '30s', target: 0 },
    ],
};

const JWT_TOKEN = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaXNoYWlsIiwiaWF0IjoxNzc4NjU3NjgxLCJleHAiOjE3Nzg2NTg1ODF9.rrRDtHY-MJrFJHZiK_ku8DBo5x0eQDHMh6XFm2-mNa1t9yi-z-a1OU848s3oZ-ndX7TrWBWWQs5TrcBh67b5PQ';

export default function () {
    const url = 'http://localhost:8080/api/v1/tracks';

    const params = {
        headers: {
            'Authorization': `Bearer ${JWT_TOKEN}`,
            'Content-Type': 'application/json',
        },
    };

    const res = http.get(url, params);

    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    sleep(0.5);
}